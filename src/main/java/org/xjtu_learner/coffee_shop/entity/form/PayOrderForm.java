package org.xjtu_learner.coffee_shop.entity.form;


import lombok.Data;
import org.xjtu_learner.coffee_shop.common.enums.PaymentMode;

import java.io.Serializable;

@Data
public class PayOrderForm implements Serializable  {

    /**
     * 订单id
     */
    private Integer orderId;
}
