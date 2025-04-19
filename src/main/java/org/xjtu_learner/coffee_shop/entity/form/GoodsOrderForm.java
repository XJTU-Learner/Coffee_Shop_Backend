package org.xjtu_learner.coffee_shop.entity.form;


import lombok.Data;
import org.xjtu_learner.coffee_shop.common.enums.PaymentMode;

import java.util.List;

@Data
public class GoodsOrderForm {

    /**
     * 商家id
     */
    private Integer merchantId;


    /**
     * 备注
     */
    private String remark;


    /**
     * 使用的满减优惠券id
     */
    private Integer couponsMemberRelationId;



    /**
     * 支付方式
     */
    private PaymentMode paymentMode;


    /**
     * 商品列表详情
     */
    List<GoodsOrderDetailForm> detail;

}
