package org.xjtu_learner.coffee_shop.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum PaymentMode {
    
    BALANCE(0, "平台余额支付"),
    WECHAT(1, "微信支付");

    @EnumValue
    private final int value;
    private final String type;

    PaymentMode(int i, String type) {
        this.value = i;
        this.type = type;
    }
}
