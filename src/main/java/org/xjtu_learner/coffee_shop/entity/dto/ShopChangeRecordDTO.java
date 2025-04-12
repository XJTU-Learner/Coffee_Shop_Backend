package org.xjtu_learner.coffee_shop.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.xjtu_learner.coffee_shop.common.enums.AuditStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShopChangeRecordDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 申请商户id
     */
    private Integer merchantId;

    /**
     * 门店信息id
     */
    private Integer shopId;

    /**
     * 门店名称
     */
    private String nickname;

    /**
     * 变更前省份
     */
    private String province;

    /**
     * 变更后省份
     */
    private String newProvince;

    /**
     * 变更前城市
     */
    private String city;

    /**
     * 变更后城市
     */
    private String newCity;

    /**
     * 变更前区/县
     */
    private String area;

    /**
     * 变更后区/县
     */
    private String newArea;

    /**
     * 变更前详细地址
     */
    private String street;

    /**
     * 变更后详细地址
     */
    private String newStreet;

    /**
     * 变更前门牌号
     */
    private String houseNumber;

    /**
     * 变更后门牌号
     */
    private String newHouseNumber;

    /**
     * 变更前门脸照片
     */
    private String shopImg;

    /**
     * 变更后门脸照片
     */
    private String newShopImg;

    /**
     * 变更前联系人姓名
     */
    private String contactRealname;

    /**
     * 变更后联系人姓名
     */
    private String newContactRealname;

    /**
     * 变更前联系电话
     */
    private String contactPhone;

    /**
     * 变更后联系人电话
     */
    private String newContactPhone;

    /**
     * 变更前门店简介
     */
    private String briefIntroduction;

    /**
     * 变更后门店简洁
     */
    private String newBriefIntroduction;

    /**
     * 变更前营业执照
     */
    private String businessLicense;

    /**
     * 变更后营业执照
     */
    private String newBusinessLicense;

    /**
     * 变更前店铺营业开始时间
     */
    private String openTime;

    /**
     * 变更后店铺营业开始时间
     */
    private String newOpenTime;

    /**
     * 变更前店铺营业结束时间
     */
    private String closeTime;

    /**
     * 变更后店铺营业结束时间
     */
    private String newCloseTime;

    /**
     * 变更前经度
     */
    private BigDecimal longitude;

    /**
     * 变更后经度
     */
    private BigDecimal newLongitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 变更后纬度
     */
    private BigDecimal newLatitude;

    /**
     * 审批状态 1=审核中 2=审核成功 3=审核失败
     */
    @TableField("audit_status")
    private AuditStatus auditStatus;


    /**
     * 创建时间
     */
    @TableField("create_at")
    private LocalDateTime createAt;

}
