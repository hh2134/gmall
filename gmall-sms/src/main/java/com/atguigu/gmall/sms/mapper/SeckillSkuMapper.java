package com.atguigu.gmall.sms.mapper;

import com.atguigu.gmall.sms.entity.SeckillSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动商品关联
 * 
 * @author eyvren
 * @email eyvren@atguigu.com
 * @date 2023-03-07 20:48:44
 */
@Mapper
public interface SeckillSkuMapper extends BaseMapper<SeckillSkuEntity> {
	
}
