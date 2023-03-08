package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.CategoryBrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌分类关联
 * 
 * @author eyvren
 * @email eyvren@atguigu.com
 * @date 2023-03-07 09:14:50
 */
@Mapper
public interface CategoryBrandMapper extends BaseMapper<CategoryBrandEntity> {
	
}
