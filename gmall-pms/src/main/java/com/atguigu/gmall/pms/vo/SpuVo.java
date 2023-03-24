package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuEntity;
import lombok.Data;

import java.util.List;

/**
 * @ClassName SpuVo
 * @Author 鳄鱼魜
 * @Date 2023/3/15 19:50
 * @Version 1.0
 * @Description SpuEntity扩展字段
 */
@Data
public class SpuVo extends SpuEntity {

    // 可能有多张海报且都是字符串格式，所以用 List<String> 来接收
    private List<String> spuImages;  // 海报信息

    private List<SpuAttrValueVo> baseAttrs;  // 基本属性

    private List<SkuVo> skus;   // sku信息
}