package org.xjtu_learner.coffee_shop.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.xjtu_learner.coffee_shop.common.enums.AuditStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 门店重要信息变更记录表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-06
 */
@Getter
@Setter
@ToString
@TableName("tb_shop_change_record")
public class ShopChangeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 申请商户id
     */
    @TableField("merchant_id")
    private Integer merchantId;


    /**
     * 变更前省份
     */
    @TableField("province")
    private String province;

    /**
     * 变更后省份
     */
    @TableField("new_province")
    private String newProvince;

    /**
     * 变更前城市
     */
    @TableField("city")
    private String city;

    /**
     * 变更后城市
     */
    @TableField("new_city")
    private String newCity;

    /**
     * 变更前区/县
     */
    @TableField("area")
    private String area;

    /**
     * 变更后区/县
     */
    @TableField("new_area")
    private String newArea;

    /**
     * 变更前详细地址
     */
    @TableField("street")
    private String street;

    /**
     * 变更后详细地址
     */
    @TableField("new_street")
    private String newStreet;

    /**
     * 变更前门牌号
     */
    @TableField("house_number")
    private String houseNumber;

    /**
     * 变更后门牌号
     */
    @TableField("new_house_number")
    private String newHouseNumber;

    /**
     * 变更前门脸照片
     */
    @TableField("shop_img")
    private String shopImg;

    /**
     * 变更后门脸照片
     */
    @TableField("new_shop_img")
    private String newShopImg;

    /**
     * 变更前联系人姓名
     */
    @TableField("contact_realname")
    private String contactRealname;

    /**
     * 变更后联系人姓名
     */
    @TableField("new_contact_realname")
    private String newContactRealname;

    /**
     * 变更前联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 变更后联系人电话
     */
    @TableField("new_contact_phone")
    private String newContactPhone;

    /**
     * 变更前门店简介
     */
    @TableField("brief_introduction")
    private String briefIntroduction;

    /**
     * 变更后门店简洁
     */
    @TableField("new_brief_introduction")
    private String newBriefIntroduction;

    /**
     * 变更前营业执照
     */
    @TableField("business_license")
    private String businessLicense;

    /**
     * 变更后营业执照
     */
    @TableField("new_business_license")
    private String newBusinessLicense;

    /**
     * 变更前店铺营业开始时间
     */
    @TableField("open_time")
    private LocalTime openTime;

    /**
     * 变更后店铺营业开始时间
     */
    @TableField("new_open_time")
    private LocalTime newOpenTime;

    /**
     * 变更前店铺营业结束时间
     */
    @TableField("close_time")
    private LocalTime closeTime;

    /**
     * 变更后店铺营业结束时间
     */
    @TableField("new_close_time")
    private LocalTime newCloseTime;

    /**
     * 变更前经度
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 变更后经度
     */
    @TableField("new_longitude")
    private BigDecimal newLongitude;

    /**
     * 纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 变更后纬度
     */
    @TableField("new_latitude")
    private BigDecimal newLatitude;

    /**
     * 审批状态 1=审核中 2=审核成功 3=审核失败
     */
    @TableField("audit_status")
    private AuditStatus auditStatus;

    /**
     * 审核员
     */
    @TableField("auditor")
    private Integer auditor;

    /**
     * 审批失败原因
     */
    @TableField("audit_reason")
    private String auditReason;

    /**
     * 审批时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 创建时间
     */
    @TableField("create_at")
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    @TableField("update_at")
    private LocalDateTime updateAt;
}
