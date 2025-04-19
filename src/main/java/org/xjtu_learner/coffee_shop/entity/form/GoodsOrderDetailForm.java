package org.xjtu_learner.coffee_shop.entity.form;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsOrderDetailForm {

    /**
     * 商品id
     */
    private Integer goodsId;


    /**
     * 详情
     */
    private String info;


    /**
     * 使用的折扣券优惠券用户关系id
     */
    private Integer couponsMemberRelationId;

}
