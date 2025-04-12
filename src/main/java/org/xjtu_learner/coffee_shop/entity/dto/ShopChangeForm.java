package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopChangeForm {

    /**
     * 变更后省份
     */
    private String newProvince;

    /**
     * 变更后城市
     */
    private String newCity;

    /**
     * 变更后区/县
     */
    private String newArea;

    /**
     * 变更后详细地址
     */
    private String newStreet;

    /**
     * 变更后门牌号
     */
    private String newHouseNumber;

    /**
     * 变更后门脸照片
     */
    private String newShopImg;

    /**
     * 变更后联系人姓名
     */
    private String newContactRealname;

    /**
     * 变更后联系人电话
     */
    private String newContactPhone;

    /**
     * 变更后门店简介
     */
    private String newBriefIntroduction;

    /**
     * 变更后营业执照
     */
    private String newBusinessLicense;

    /**
     * 变更后店铺营业开始时间
     */
    private String newOpenTime;

    /**
     * 变更后店铺营业结束时间
     */
    private String newCloseTime;

    /**
     * 变更后经度
     */
    private BigDecimal newLongitude;

    /**
     * 变更后纬度
     */
    private BigDecimal newLatitude;
}
