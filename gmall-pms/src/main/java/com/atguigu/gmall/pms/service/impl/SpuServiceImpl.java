package com.atguigu.gmall.pms.service.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.*;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.beans.Transient;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescMapper spuDescMapper;

    /*
    * 建议能用 Mapper 就不要用 Service，最好不要在 Service 中注入其他 Service
    * */
    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private GmallSmsClient gmallSmsClient;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }


    /*
     * categoryId 传入品牌id
     * this.page 调用了 IService 中的 page(分页条件，查询条件) 分页查询方法
     * return PageResultVo(IPage page)
     * */
    @Override
    public PageResultVo querySpuByCategoryIdPage(Long categoryId, PageParamVo paramVo) {
        QueryWrapper<SpuEntity> spuEntityQueryWrapper = new QueryWrapper<>();

        // 查询的sql语句.查本类：select * from pms_spu where category_id='225' AND (id='7'OR name LIKE '%7%');
        // 查询的sql语句.查全站：select * from pms_spu where (id='7'OR name LIKE '%7%');
        // 1.添加查询条件
        if (categoryId != 0) {
            spuEntityQueryWrapper.eq("category_id",categoryId);
        }

        // 2.拿到关键字
        String key = paramVo.getKey();
        //
        /*
        * StringUtil 选择 com.alibaba.csp.sentinel.util.StringUtil
        * isNotBlank 做了一个空格Whitespace的判断
        * and(函数式接口)
        * 函数式接口类型有以下四中种
        * 消费型函数接口：  有参数没有返回结果集   t ->
        * 供给型函数接口：  没有参数有返回结果集   () -> return
        * 函数型函数接口：  既有参数又有返回结果集   t -> return
        * 断言型函数接口：  有参数有返回结果集，但是返回结果集是 boolean 类型   t -> return boolean
        * */
        if(StringUtil.isNotBlank(key)){
            spuEntityQueryWrapper.and(t ->t.eq("id" , key).or().like("name",key));
        }

        // 翻页查询
        IPage<SpuEntity> page = this.page(
                // 翻页对象
                paramVo.getPage(),
                // 查询条件
                spuEntityQueryWrapper
        );

        return new PageResultVo(page);
    }

    /*
    * 大保存方法实现
    * @Transactional：事务的注解
    * */
    @Transactional
    @Override
    public void bigSave(SpuVo spu) {
        // 1.保存spu相关表
        // 1.1 保存pms_spu
        // 1.1.1 设置创建时间
        spu.setCreateTime(new Date());      // Data() 获取系统时间
        // 1.1.2 设置更新时间
        spu.setUpdateTime(spu.getCreateTime());     // 不能直接去 new Date()，会出现毫秒级别的差异，会导致创建时出现时间不一样
        this.save(spu);     // 将数据保存到数据库的表中
        Long spuId = spu.getId();  //刚刚新增好后，主键回写就可以拿到spuId

        // 1.2 保存pms_spu_desc
        List<String> spuImages = spu.getSpuImages();// 保存时的海报信息

        SpuDescEntity spuDescEntity = new SpuDescEntity();
        spuDescEntity.setSpuId(spuId);  //因为表pms_spu_desc没有自动递增（自动递增会导致和其它表对不上，所以没有设置），所以进行手动设置
        if(!CollectionUtils.isEmpty(spuImages)){    // 做一个非空判断
            spuDescEntity.setSpuId(spuId);
            spuDescEntity.setDecript(StringUtils.join(spuImages, ","));
            this.spuDescMapper.insert(spuDescEntity);
        }

        // 1.3 保存pms_spu_attr_value
        List<SpuAttrValueVo> baseAttrs = spu.getBaseAttrs();

        if(!CollectionUtils.isEmpty(baseAttrs)){    // 做一个非空判断

            List<SpuAttrValueEntity> collect = baseAttrs.stream().map(spuAttrValueVo -> {
                SpuAttrValueEntity spuAttrValueEntity = new SpuAttrValueEntity();
                BeanUtils.copyProperties(spuAttrValueVo, spuAttrValueEntity);   // 将 spuAttrValueVo copy spuAttrValueEntity，只有 Vo里面的字段和 Entity 字段一致，才可以使用 BeanUtils.copyProperties() 进行一一拷贝
                spuAttrValueEntity.setSpuId(spuId); // 要手动保存 pms_spu_attr_value 表，的 spu_id 字段，因为前端没有传这个字段过来
                return spuAttrValueEntity;
            }).collect(Collectors.toList());    // 将 Vo 集合转换成 Entity 集合
            this.spuAttrValueService.saveBatch(collect);       // saveBatch()批量保存到基本属性表，saveBatch 需要一个 Entity 集合，所以要将 Vo 集合转换成一个 Entity 集合
        }
        // 2.保存sku相关表
        List<SkuVo> skus = spu.getSkus();   //获取sku信息
        if(CollectionUtils.isEmpty(skus)){
            return;
        }
        skus.forEach(skuVo -> {
            // 2.1 保存pms_sku
            skuVo.setSpuId(spuId);  // 这个数据前端没有，要手动设置
            skuVo.setBrandId(spu.getBrandId());
            skuVo.setCategoryId(spu.getCategoryId());
            List<String> images = skuVo.getImages();    // 获取图片列表
            if(!CollectionUtils.isEmpty(images)){
                skuVo.setDefaultImage(StringUtils.isBlank(skuVo.getDefaultImage()) ? images.get(0) : skuVo.getDefaultImage());   // 设置默认图片，如果没有传递了默认图片，则使用第一张图片作为默认图片，否则使用传递过来的图片作为默认图片
            }
            this.skuMapper.insert(skuVo);   // 保存sku信息
            Long skuId = skuVo.getId();   // 通过主键回写拿到 skuId 因为后续访问各种信息的时候我们都需要这个 skuId
            // 2.2 保存pms_images
            // 把图片的地址集合 转化成 图片的对象集合
            if(!CollectionUtils.isEmpty(images)){
                this.skuImagesService.saveBatch(images.stream().map(image->{
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setUrl(image);
                    skuImagesEntity.setDefaultStatus(StringUtil.equals(skuVo.getDefaultImage(), image) ? 1 : 0);    //判断是否是默认图片，如果是则为1，否则为0
                    return skuImagesEntity;
                }).collect(Collectors.toList()));
            }
            // 2.3 保存pms_sku_attr_value
            List<SkuAttrValueEntity> saleAttrs = skuVo.getSaleAttrs();
            saleAttrs.forEach(skuAttrValueEntity -> {
                skuAttrValueEntity.setSkuId(skuId);     // 这个数据前端没有要手动添加
            });
            this.skuAttrValueService.saveBatch(saleAttrs);

            // 3.保存营销信息相关表
            // 在 gmall-sms 服务中写
                // 3.1 保存sms_sku_bounds
                // 3.2 保存sms_sku_full_reduction
                // 3.3 保存sms_sku_ladder
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(skuVo, skuSaleVo);     // 将 skuVo 拷贝到 skuSaleVo
            skuSaleVo.setSkuId(skuId);
            this.gmallSmsClient.saveSales(skuSaleVo);
        });
    }

}