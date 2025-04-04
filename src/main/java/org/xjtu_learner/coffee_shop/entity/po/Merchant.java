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
 * 商家账号表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_merchant")
public class Merchant implements Serializable {

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
     * 手机号码
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 用户密码
     */
    @TableField("password")
    private String password;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 权限
     */
    @TableField("roles")
    private String roles;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 证件类型
     */
    @TableField("certificate_type")
    private String certificateType;

    /**
     * 证件照片
     */
    @TableField("certificate_img")
    private String certificateImg;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 身份证号码
     */
    @TableField("id_card")
    private String idCard;

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
     * 微信账号
     */
    @TableField("wechat_account")
    private String wechatAccount;

    /**
     * 余额
     */
    @TableField("balance")
    private BigDecimal balance;

    /**
     * 可提现余额
     */
    @TableField("withdrawable_balance")
    private BigDecimal withdrawableBalance;

    /**
     * 冻结余额
     */
    @TableField("frozen_balance")
    private BigDecimal frozenBalance;

    /**
     * 用户下单冻结资金
     */
    @TableField("order_frozen_balance")
    private BigDecimal orderFrozenBalance;

    /**
     * 绑定的小程序用户id(用于发送微信公众号消息、进行商家端余额提现)
     */
    @TableField("member_id")
    private Integer memberId;

    /**
     * 审批状态 1=审核中 2=审核成功 3=审核失败
     */
    @TableField("audit_status")
    private Integer auditStatus;

    /**
     * 是否禁用 0=启用 1=禁用
     */
    @TableField("is_disabled")
    private Boolean isDisabled;

    /**
     * 是否删除 0=正常 1=删除
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 注册时间
     */
    @TableField("register_time")
    private LocalDateTime registerTime;

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
