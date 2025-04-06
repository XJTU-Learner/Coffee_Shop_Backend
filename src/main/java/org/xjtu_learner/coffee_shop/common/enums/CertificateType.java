package org.xjtu_learner.coffee_shop.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum CertificateType {
    // 个人证件类型
    ID_CARD(1,"身份证"),
    HONGKONG_MACAO_PASS(2,"港澳居民来往内地通行证"),
    TAIWAN_PASS(3,"台湾居民来往大陆通行证"),
    FOREIGNER_PERMANENT_RESIDENCE_ID(4,"外国人永久居留身份证"),
    HONGKONG_MACAO_TAIWAN_RESIDENT_PERMIT(5,"港澳台居民居住证");

    @EnumValue
    private final int value;
    private final String type;

    CertificateType(int value, String type) {
        this.value = value;
        this.type = type;
    }

    /**
     * 根据value查找对应的CertificateType枚举值
     * @param value 枚举值对应的数字
     * @return 对应的CertificateType枚举值
     * @throws IllegalArgumentException 如果找不到对应的枚举值
     */
    public static CertificateType of(int value) {
        for (CertificateType type : CertificateType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value + ". No matching CertificateType found.");
    }
}
