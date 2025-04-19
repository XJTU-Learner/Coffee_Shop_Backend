package org.xjtu_learner.coffee_shop.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum OrderStatus {

    UNPAID(0, "未付款"),
    IN_PRODUCTION(1, "制作中"),
    WAIT_RECEIVE(2, "待自取"),
    COMPLETED(3, "已完成"),
    AFTER_SALES_PROCESSING(4, "售后处理中"),
    AFTER_SALES_COMPLETED(5, "售后处理完成"),
    CANCEL(6, "已取消"),
    TIMED_OUT(7, "超时取消");

    @EnumValue
    private final int value;
    private final String type;


    OrderStatus(int value, String type) {
        this.value = value;
        this.type = type;
    }
}
