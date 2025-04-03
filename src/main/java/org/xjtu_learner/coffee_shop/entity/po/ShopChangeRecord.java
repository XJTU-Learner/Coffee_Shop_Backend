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
 * 门店重要信息变更记录表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
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
     * 门店信息id
     */
    @TableField("shop_id")
    private Integer shopId;

    /**
     * 门店名称
     */
    @TableField("name")
    private String name;

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
     * 门脸照片
     */
    @TableField("shop_img")
    private String shopImg;

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
     * 申请变更内容/标识哪些字段进行了变更
     */
    @TableField("apply_change_content")
    private String applyChangeContent;

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
