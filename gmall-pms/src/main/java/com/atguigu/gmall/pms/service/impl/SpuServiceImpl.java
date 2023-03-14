package com.atguigu.gmall.pms.service.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.service.SpuService;



@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

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

}