package org.xjtu_learner.coffee_shop.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum AuditStatus {
    ONGOING(1, "进行中"),
    SUCCEED(2, "审核成功"),
    FAILED(3, "审核失败");

    @EnumValue
    private final int value;
    private final String type;

    AuditStatus(int value, String type) {
        this.value = value;
        this.type = type;
    }
}
