package org.xjtu_learner.coffee_shop.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.xjtu_learner.coffee_shop.common.enums.AuditStatus;
import org.xjtu_learner.coffee_shop.common.enums.CertificateType;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * <p>
 * 商户重要信息变更表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-06
 */
@Getter
@Setter
@ToString
@TableName("tb_merchant_change_record")
public class MerchantChangeRecord implements Serializable {

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
     * 变更前手机号码
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 变更后手机号码
     */
    @TableField("new_mobile")
    private String newMobile;

    /**
     * 变更前门店名称
     */
    @TableField("nickname")
    private String nickName;

    /**
     * 变更后门店名称
     */
    @TableField("new_nickname")
    private String newNickname;

    /**
     * 变更前证件类型
     */
    @TableField("certificate_type")
    private CertificateType certificateType;

    /**
     * 变更后证件类型
     */
    @TableField("new_certificate_type")
    private CertificateType newCertificateType;

    /**
     * 变更前证件照片
     */
    @TableField("certificate_img")
    private String certificateImg;

    /**
     * 变更后证件照片
     */
    @TableField("new_certificate_img")
    private String newCertificateImg;

    /**
     * 变更前真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 变更后真实姓名
     */
    @TableField("new_real_name")
    private String newRealName;

    /**
     * 变更前身份证号码
     */
    @TableField("id_card")
    private String idCard;

    /**
     * 变更后身份证号码
     */
    @TableField("new_id_card")
    private String newIdCard;

    /**
     * 变更前开户银行
     */
    @TableField("opening_bank")
    private String openingBank;

    /**
     * 变更后开户银行
     */
    @TableField("new_opening_bank")
    private String newOpeningBank;

    /**
     * 变更前银行卡号
     */
    @TableField("bank_card")
    private String bankCard;

    /**
     * 变更后银行卡号
     */
    @TableField("new_bank_card")
    private String newBankCard;

    /**
     * 变更前微信账号
     */
    @TableField("wechat_account")
    private String wechatAccount;

    /**
     * 变更后微信账号
     */
    @TableField("new_wechat_account")
    private String newWechatAccount;

    /**
     * 变更前支付宝账号
     */
    @TableField("alipay_account")
    private String alipayAccount;

    /**
     * 变更后阿里账号
     */
    @TableField("new_alipay_account")
    private String newAlipayAccount;

    /**
     * 审核员
     */
    @TableField("auditor")
    private Integer auditor;

    /**
     * 审批状态 1=审核中 2=审核成功 3=审核失败
     */
    @TableField("audit_status")
    private AuditStatus auditStatus;

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
