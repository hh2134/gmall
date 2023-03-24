package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import lombok.Data;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @ClassName SpuAttrValueVo
 * @Author 鳄鱼魜
 * @Date 2023/3/15 20:06
 * @Version 1.0
 * @Description SpuAttrValueEntity扩展字段
 */

public class SpuAttrValueVo extends SpuAttrValueEntity {

    // 使 JSON 字符 valueSelected 直接存到中的 attr_Value 字段中
    // 因为属性可能有多选且都是字符串来接收，所以用 List<String> 来接收 JSON 数据
    public void setValueSelected(List<String> valueSelected) {

        // 做一个判空，如果为空则 return
        if(CollectionUtils.isEmpty(valueSelected)){
            return;
        }

        // StringUtils.join 可以用来拼接字符串
        // StringUtils.split 可以用来分割字符串
        // 多选的属性用 , 来分割
        this.setAttrValue(StringUtils.join(valueSelected, ','));
    }
}