package org.xjtu_learner.coffee_shop.entity.dto;

import lombok.Data;

@Data
public class MerchantChangeFormDTO {

    /**
     * 变更后手机号码
     */
    private String newMobile;

    /**
     * 变更后门店名称
     */
    private String newNickname;

    /**
     * 变更后证件类型
     */
    private Integer newCertificateType;

    /**
     * 变更后证件照片
     */
    private String newCertificateImg;

    /**
     * 变更后真实姓名
     */
    private String newRealName;

    /**
     * 变更后身份证号码
     */
    private String newIdCard;

    /**
     * 变更后开户银行
     */
    private String newOpeningBank;

    /**
     * 变更后银行卡号
     */
    private String newBankCard;

    /**
     * 变更后微信账号
     */
    private String newWechatAccount;

    /**
     * 变更后阿里账号
     */
    private String newAlipayAccount;
}
