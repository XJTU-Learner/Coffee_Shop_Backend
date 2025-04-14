package org.xjtu_learner.coffee_shop.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum PreferentialType {
    DISCOUNT(0, "折扣"),
    REDUCTION(1, "满减");

    @EnumValue
    private final int value;
    private final String type;

    PreferentialType(int value, String type) {
        this.value = value;
        this.type = type;
    }
}
