package org.xjtu_learner.coffee_shop.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum MemberRegisterWay {
    MOBILE(1),
    WECHAT(2);

    @EnumValue
    private final int value;

    MemberRegisterWay(int value) {
        this.value = value;
    }
}
