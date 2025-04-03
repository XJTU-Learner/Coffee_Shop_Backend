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
 * 商家提现记录表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_merchant_withdraw_record")
public class MerchantWithdrawRecord implements Serializable {

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
     * 提现金额
     */
    @TableField("withdraw_amount")
    private BigDecimal withdrawAmount;

    /**
     * 平台手续费/服务费
     */
    @TableField("platform_fee")
    private BigDecimal platformFee;

    /**
     * 实际到账金额
     */
    @TableField("actual_amount")
    private BigDecimal actualAmount;

    /**
     * 审核状态 1=平台处理中 2=到账成功 3=审核不通过
     */
    @TableField("audit_status")
    private Integer auditStatus;

    /**
     * 审核不通过原因
     */
    @TableField("audit_reason")
    private String auditReason;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 打款方式/到账方式 1=微信 2=支付宝 3=银行
     */
    @TableField("payment_mode")
    private Integer paymentMode;

    /**
     * 开户行
     */
    @TableField("opening_bank_address")
    private String openingBankAddress;

    /**
     * 开户银行名称
     */
    @TableField("opening_bank_name")
    private String openingBankName;

    /**
     * 银行卡号
     */
    @TableField("bank_card")
    private String bankCard;

    /**
     * 支付宝账号
     */
    @TableField("alipay_account")
    private String alipayAccount;

    /**
     * 微信账号
     */
    @TableField("wechat_account")
    private String wechatAccount;

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
