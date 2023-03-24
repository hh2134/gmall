package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.AttrGroupMapper;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroupEntity> implements AttrGroupService {

    /*
    * @Autowired 它可以对类成员变量、方法及构造函数进行标注，完成自动装配的工作，通过 @Autowired的使用来消除 set ，get方法
    * queryGroupsWithAttrsByCid 要用到 AttrMapper 表类，来创建list集合，所以要自动装配 AttrMapper
    * */
    @Autowired
    private AttrMapper attrMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrGroupEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageResultVo(page);
    }

    /*
    * 有两种查法
    * 1.关联：用 pms_attr_group 关联 pms_attr 进行查询
    * 2.分布查询：先查询 pms_attr_group 表，连接字段 category_id = catId ；在通过 pms_attr 表，连接字段 group_id = pms_attr_group.id
    * 推荐使用：分布查询，因为在互联网项目中，数据表都比较大，关联查询消耗的资源多，导致性能降低
    * .eq("type",1) 表示我只要spu的基本属性属性，在表 pms_attr 中字段 type ，0-销售属性、1-基本属性、2-既是销售属性又是基本属性
    * */
    @Override
    public List<AttrGroupEntity> queryAttrGroupByCatId(Long catId) {
        // 查询分组
        List<AttrGroupEntity> attrGroupEntityList = this.list(new QueryWrapper<AttrGroupEntity>().eq("category_id", catId));

        // 进行判空
        if(CollectionUtils.isEmpty(attrGroupEntityList)){
            return attrGroupEntityList;
        }

        // 遍历分组查询每一个分组下的规格参数列表
        attrGroupEntityList.forEach(attrGroupEntity -> {
            List<AttrEntity> attrEntityList = this.attrMapper.selectList(new QueryWrapper<AttrEntity>().eq("group_id", attrGroupEntity.getId()).eq("type",1));
            attrGroupEntity.setAttrEntities(attrEntityList);
        });

        return attrGroupEntityList;
    }

}