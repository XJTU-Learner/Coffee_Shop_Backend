package org.xjtu_learner.coffee_shop.common.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum PaymentStatus {
    UNPAID(0, "未支付"),
    PAID(1, "已支付"),
    CANCEL(2, "已取消"),
    TIMED_OUT(3, "超时取消");
    @EnumValue
    private final int value;
    private final String type;

    PaymentStatus(int i, String type) {
        this.value = i;
        this.type = type;
    }
}
