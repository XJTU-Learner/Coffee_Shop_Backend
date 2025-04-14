package org.xjtu_learner.coffee_shop.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum OrderStatus {

    WAIT_PAY(0),
    Making(1),
    WaitReceive(2),
    Received(3),
    AfterSale(4),
    AfterSaleFinsh(5),
    Cancel(6);

    @EnumValue
    private final int value;


    OrderStatus(int value) {
        this.value = value;
    }
}
