package org.xjtu_learner.coffee_shop.entity.form;

import lombok.Data;

@Data
public class GoodsOrderForm {

    //商品id
    int goods_id;

    //商品数量
    int count;

    //是否使用优惠券
    Boolean is_used_coupons;

    //实际价格
    double actual_price;

    //使用用户优惠券id（折扣券）
    int coupons_id;

}
