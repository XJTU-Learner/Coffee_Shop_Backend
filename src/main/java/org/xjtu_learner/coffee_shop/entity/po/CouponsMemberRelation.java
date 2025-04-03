package org.xjtu_learner.coffee_shop.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * <p>
 * 优惠卷用户关联表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_coupons_member_relation")
public class CouponsMemberRelation implements Serializable {

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
     * 优惠卷id
     */
    @TableField("coupons_id")
    private Integer couponsId;

    /**
     * 优惠卷名称
     */
    @TableField("coupons_name")
    private String couponsName;

    /**
     * 生效时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 过期时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 是否已经使用，0=未使用，1=已使用
     */
    @TableField("is_used")
    private Boolean isUsed;

    /**
     * 是否过期，0=未过期，1=已过期
     */
    @TableField("is_expired")
    private Boolean isExpired;

    /**
     * 是否有效，0-否，1-是
     */
    @TableField("is_valid")
    private Boolean isValid;

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
