package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 门店名称（来自tb_merchant表）
     */
    private String nickname;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区/县
     */
    private String area;

    /**
     * 详细地址
     */
    private String street;

    /**
     * 门牌号
     */
    private String houseNumber;

    /**
     * 门脸照片
     */
    private String shopImg;

    /**
     * 联系人姓名
     */
    private String contactRealname;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 门店简介
     */
    private String briefIntroduction;


    /**
     * 店铺营业开始时间
     */
    private String openTime;

    /**
     * 店铺营业结束时间
     */
    private String closeTime;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 是否营业 0=否 1=是
     */
    private Boolean isOpen;
}
