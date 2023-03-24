package com.atguigu.gmall.sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName SkuSaleVo
 * @Author 鳄鱼魜
 * @Date 2023/3/17 19:49
 * @Version 1.0
 * @Description 自己封装一个SkuVo字段
 */
@Data
public class SkuSaleVo {

    private Long skuId;

    // 积分优惠相关字段，在数据库 guli_sms 的 sms_sku_bounds 表里
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;
    /**
     * 优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
     */
    // "work":[0,0,1,1]，因为传过来的参数是一个集合所以要使用 List<> 类型
    private List<Integer> work;

    // 打折优惠信息相关的字段，在数据库 guli_sms 的 sms_sku_ladder 表中
    /**
     * 满几件
     */
    private Integer fullCount;
    /**
     * 打几折
     */
    private BigDecimal discount;
    /**
     * 是否叠加其他优惠[0-不可叠加，1-可叠加]
     */
    // 前端传过来的数据中有 fullAddOther 和 ladderAddOther 字段，为了区别开了加了前缀 full 和 ladder ，ladderAddOther 想要传到 sms_sku_ladder 表中 add_Other 字段上，这个别名要与页面一致
    private Integer ladderAddOther;

    // 满减优惠相关的字段，在数据库 guli_sms 的 sms_sku_bounds 表中
    /**
     * 满多少
     */
    private BigDecimal fullPrice;
    /**
     * 减多少
     */
    private BigDecimal reducePrice;
    /**
     * 是否参与其他优惠
     */
    // 这个是用来接收 fullAddOther 字段到 sms_sku_bounds 表中的 add_Other，别名与传参一致
    private Integer fullAddOther;

}