package org.xjtu_learner.coffee_shop.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum PayMode {
    
    Balance(0),//平台余额支付
    WeChat(1); //微信支付

    @EnumValue
    private final int value;

    PayMode(int i) {
        this.value = i;
    }
}
