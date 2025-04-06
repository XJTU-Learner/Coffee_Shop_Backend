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
 * @since 2025-04-06
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
     * 店铺营业开始时间
     */
    @TableField("open_time")
    private String openTime;

    /**
     * 店铺营业结束时间
     */
    @TableField("close_time")
    private String closeTime;

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
     * 是否营业 0=否 1=是
     */
    @TableField("is_open")
    private Boolean isOpen;

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
