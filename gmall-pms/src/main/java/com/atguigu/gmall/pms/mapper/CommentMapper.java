package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.CommentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价
 * 
 * @author eyvren
 * @email eyvren@atguigu.com
 * @date 2023-03-07 09:14:50
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {
	
}
