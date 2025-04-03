package org.xjtu_learner.coffee_shop.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * <p>
 * 门店表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_shop")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商家id
     */
    @TableField("merchant_id")
    private Integer merchantId;

    /**
     * 门店名称
     */
    @TableField("name")
    private String name;

    /**
     * 门店编号
     */
    @TableField("code")
    private String code;

    /**
     * 省份
     */
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 区/县
     */
    @TableField("area")
    private String area;

    /**
     * 详细地址
     */
    @TableField("street")
    private String street;

    /**
     * 门牌号
     */
    @TableField("house_number")
    private String houseNumber;

    /**
     * 店铺是否营业 0=否 1=是
     */
    @TableField("is_operating")
    private Boolean isOperating;

    /**
     * 店铺营业开始时间
     */
    @TableField("start_time")
    private String startTime;

    /**
     * 店铺营业结束时间
     */
    @TableField("end_time")
    private String endTime;

    /**
     * 门脸照片
     */
    @TableField("shop_img")
    private String shopImg;

    /**
     * 审批状态 1=审核中 2=审核成功 3=审核失败
     */
    @TableField("audit_status")
    private Integer auditStatus;

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
     * 联系人姓名
     */
    @TableField("contact_realname")
    private String contactRealname;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 门店公告
     */
    @TableField("announcement")
    private String announcement;

    /**
     * 门店简介
     */
    @TableField("brief_introduction")
    private String briefIntroduction;

    /**
     * 营业执照
     */
    @TableField("business_license")
    private String businessLicense;

    /**
     * 身份证正面
     */
    @TableField("id_card_front_side")
    private String idCardFrontSide;

    /**
     * 身份证反面
     */
    @TableField("id_card_back_side")
    private String idCardBackSide;

    /**
     * 状态 1=未运营 2=运营中 3=整改中 4=闭店
     */
    @TableField("status")
    private Integer status;

    /**
     * 经度
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;

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
