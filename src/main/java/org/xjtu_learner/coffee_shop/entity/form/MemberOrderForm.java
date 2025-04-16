package org.xjtu_learner.coffee_shop.entity.form;


import lombok.Data;
import org.xjtu_learner.coffee_shop.common.enums.PayMode;

import java.util.List;

@Data
public class MemberOrderForm {


    //商家id
    int merchant_id;

    // 用户优惠券id（满减)
    int coupons_member_relation_id;


    //描述
    String description;

    //备注
    String remark;

    //支付方式

    PayMode payment_mode;

    //商品列表详情

    List<GoodsOrderForm> goodsOrderFormList;

}
