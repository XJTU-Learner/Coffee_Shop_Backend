package org.xjtu_learner.coffee_shop.entity.form;

import lombok.Data;

@Data
public class BalancePayForm {

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 支付密码
     */
    private Integer paymentPassword;
}
