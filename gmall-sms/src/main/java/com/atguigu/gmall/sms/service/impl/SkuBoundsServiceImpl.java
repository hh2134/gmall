package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.mapper.SkuFullReductionMapper;
import com.atguigu.gmall.sms.mapper.SkuLadderMapper;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SkuBoundsMapper;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.util.CollectionUtils;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsMapper, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuFullReductionMapper skuFullReductionMapper;

    @Autowired
    private SkuLadderMapper skuLadderMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuBoundsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public void saveSales(SkuSaleVo skuSaleVo) {
        // 3.1 保存sms_sku_bounds
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(skuSaleVo, skuBoundsEntity);   // 将 skuSaleVo 拷贝到 skuBoundsEntity
        // 因为 SkuSaleVo 里面的 work 是 List<Integer> 类型，而我们 SkuBoundsEntity 里面的 work 是 Integer 类型，类型不一样无法 copy，要进行手动保存
        List<Integer> work = skuSaleVo.getWork();
        // 做非空判断
        if (!CollectionUtils.isEmpty(work) && work.size() == 4){
            // 在数据库中 work 的类型 tinyint是整数数据类型，范围存储-128到127的整数 存储数据要求 优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
            skuBoundsEntity.setWork(work.get(3) *8 + work.get(2) *4 + work.get(1) * 2 + work.get(0));  // 将二进制的 work 转化成十进制放入 work
        }
        this.save(skuBoundsEntity);

        // 3.2 保存sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuSaleVo, skuFullReductionEntity);   // 将 skuSaleVo 拷贝到 skuFullReductionEntity
        skuFullReductionEntity.setAddOther(skuSaleVo.getFullAddOther());    // 在 SkuSaleVo 中的别名叫 fullAddOther，在实体类 SkuFullReductionEntity 中叫 addother，不一致所以要手动设置一下
        this.skuFullReductionMapper.insert(skuFullReductionEntity);

        // 3.3 保存sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuSaleVo,skuLadderEntity);    // 将 skuSaleVo 拷贝到 skuLadderEntity
        skuLadderEntity.setAddOther(skuSaleVo.getLadderAddOther()); // 在 skuSaleVo 类中 ladderAddOther 和 addOther 类中 addOther，别名不一致无法对应，所以要手动设置
        this.skuLadderMapper.insert(skuLadderEntity);

    }

}