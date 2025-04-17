package org.xjtu_learner.coffee_shop.entity.dto;


import lombok.Data;
import org.xjtu_learner.coffee_shop.common.enums.PayMode;

import java.io.Serializable;

@Data
public class PayOrderForm implements Serializable  {

    private static final long serialVersionUID = 1L;

    private Integer order_id;

    private Integer marchant_id;

    private double actual_price;

    private PayMode payment_mode;

}
