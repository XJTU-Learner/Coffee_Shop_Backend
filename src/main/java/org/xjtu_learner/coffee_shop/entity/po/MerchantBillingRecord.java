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
 * 商家账单记录表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_merchant_billing_record")
public class MerchantBillingRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商家账号id
     */
    @TableField("merchant_id")
    private Integer merchantId;

    /**
     * 账单类型 1=用户下单 2=商家提现 3=商家提现失败退回 4=用户申请退款 
     */
    @TableField("type")
    private Integer type;

    /**
     * 操作类型 1=加 2=减
     */
    @TableField("operate_type")
    private Integer operateType;

    /**
     * 货币类型 1=余额
     */
    @TableField("coin_type")
    private Integer coinType;

    /**
     * 增减的数值
     */
    @TableField("number")
    private BigDecimal number;

    /**
     * 账单信息
     */
    @TableField("message")
    private String message;

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
