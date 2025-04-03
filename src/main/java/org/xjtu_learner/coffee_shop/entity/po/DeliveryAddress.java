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
 * 收货地址表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_delivery_address")
public class DeliveryAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    @TableField("member_id")
    private Integer memberId;

    /**
     * 联系人姓名
     */
    @TableField("realname")
    private String realname;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

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
     * 是否默认收获地址 0=否 1=是
     */
    @TableField("is_default")
    private Boolean isDefault;

    /**
     * 联系人性别 0=无 1=先生 2=女士
     */
    @TableField("sex")
    private Integer sex;

    /**
     * 门牌号
     */
    @TableField("house_number")
    private String houseNumber;

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
