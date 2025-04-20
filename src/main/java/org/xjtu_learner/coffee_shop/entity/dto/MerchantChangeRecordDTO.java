package org.xjtu_learner.coffee_shop.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.xjtu_learner.coffee_shop.common.enums.AuditStatus;
import org.xjtu_learner.coffee_shop.common.enums.CertificateType;

import java.time.LocalDateTime;

@Data
public class MerchantChangeRecordDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 申请商户id
     */
    private Integer merchantId;

    /**
     * 变更前手机号码
     */
    private String mobile;

    /**
     * 变更后手机号码
     */
    private String newMobile;

    /**
     * 变更前门店名称
     */
    private String nickname;

    /**
     * 变更后门店名称
     */
    private String newNickname;

    /**
     * 变更前证件类型
     */
    private CertificateType certificateType;

    /**
     * 变更后证件类型
     */
    private CertificateType newCertificateType;

    /**
     * 变更前证件照片
     */
    private String certificateImg;

    /**
     * 变更后证件照片
     */
    private String newCertificateImg;

    /**
     * 变更前真实姓名
     */
    private String realName;

    /**
     * 变更后真实姓名
     */
    private String newRealName;

    /**
     * 变更前身份证号码
     */
    private String idCard;

    /**
     * 变更后身份证号码
     */
    private String newIdCard;

    /**
     * 变更前开户银行
     */
    private String openingBank;

    /**
     * 变更后开户银行
     */
    private String newOpeningBank;

    /**
     * 变更前银行卡号
     */
    private String bankCard;

    /**
     * 变更后银行卡号
     */
    private String newBankCard;

    /**
     * 变更前微信账号
     */
    private String wechatAccount;

    /**
     * 变更后微信账号
     */
    private String newWechatAccount;

    /**
     * 变更前支付宝账号
     */
    private String alipayAccount;

    /**
     * 变更后阿里账号
     */
    private String newAlipayAccount;


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
