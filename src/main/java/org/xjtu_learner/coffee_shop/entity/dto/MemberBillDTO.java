package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Data;

@Data
public class MemberBillDTO {

    private String orderId;
    private String orderTime;
    private String orderPrice;
}
