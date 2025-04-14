package org.xjtu_learner.coffee_shop.common.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum PaymentStatus {
    WaitPay(0), //等待支付
    Success(1), //支付成功
    Fail(2),//支付失败
    OverTime(3)//支付超时
    ;
    @EnumValue
    private final int value;

    PaymentStatus(int i) {
        this.value = i;
    }
}
