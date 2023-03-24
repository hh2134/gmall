package com.atguigu.gmall.pms.controller;

import java.util.List;

import com.atguigu.gmall.pms.mapper.AttrGroupMapper;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.service.AttrService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 商品属性
 *
 * @author eyvren
 * @email eyvren@atguigu.com
 * @date 2023-03-07 09:14:50
 */
@Api(tags = "商品属性 管理")
@RestController
@RequestMapping("pms/attr")
public class AttrController {

    @Autowired
    private AttrService attrService;

    @GetMapping("/category/{cid}")
    @ApiOperation("查询分类下的规格参数")
    public ResponseVo<List<AttrEntity>> queryAttrByCidTypeSearchType(

        // @RequestParam("type")Integer type 默认是必须的，而接口文档可要可不要
        //有两种处理方式
        // 1.第一种 @RequestParam("type",required = false)，required 默认是ture
        // 2.第二种 @RequestParam("type",defaultValue = )，defaultValue 表示设置默认值
        // 优先使用 defaultValue ，如果不能则使用 required
        // 这里无法使用 defaultValue
        @PathVariable("cid")Long cid,
        @RequestParam(value = "type",required = false)Integer type,
        @RequestParam(value = "searchType",required = false)Integer searchType
    ){

        List<AttrEntity> attrEntityList = this.attrService.queryAttrByCidTypeSearchType(cid,type,searchType);

        return ResponseVo.ok(attrEntityList);
    }

    /*
    * 查询组下的规格参数
    * */
    @GetMapping("/group/{gid}")
    @ApiOperation("查询组下的规格参数")
    public ResponseVo<List<AttrEntity>> queryAttrByGid(@PathVariable("gid")Long gid){
        List<AttrEntity> attrEntityList = this.attrService.list(new QueryWrapper<AttrEntity>().eq("group_id", gid));

        return ResponseVo.ok(attrEntityList);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryAttrByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = attrService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<AttrEntity> queryAttrById(@PathVariable("id") Long id){
		AttrEntity attr = attrService.getById(id);

        return ResponseVo.ok(attr);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody AttrEntity attr){
		attrService.save(attr);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody AttrEntity attr){
		attrService.updateById(attr);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		attrService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
