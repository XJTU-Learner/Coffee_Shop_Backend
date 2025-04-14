package org.xjtu_learner.coffee_shop.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum TimeLimitType {
    ABSOLUTE(1, "绝对时效"),
    RELATIVE(2, "相对时效");

    @EnumValue
    private final int value;
    private final String type;

    TimeLimitType(int value, String type) {
        this.value = value;
        this.type = type;
    }
}
