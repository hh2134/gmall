package com.atguigu.gmall.sms.mapper;

import com.atguigu.gmall.sms.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author eyvren
 * @email eyvren@atguigu.com
 * @date 2023-03-07 20:48:44
 */
@Mapper
public interface MemberPriceMapper extends BaseMapper<MemberPriceEntity> {
	
}
