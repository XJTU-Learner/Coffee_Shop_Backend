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
 * 用户表
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("tb_member")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 手机号
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 余额
     */
    @TableField("balance")
    private BigDecimal balance;

    /**
     * 积分
     */
    @TableField("points")
    private BigDecimal points;

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 性别 0=无 1=男 2=女
     */
    @TableField("sex")
    private Integer sex;

    /**
     * 电子邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 微信小程序openId
     */
    @TableField("open_id")
    private String openId;

    /**
     * 是否绑定微信 0=否 1=是
     */
    @TableField("is_bind_wx")
    private Boolean isBindWx;

    /**
     * 注册方式 1=手机验证码 2=微信一键登录
     */
    @TableField("register_way")
    private Integer registerWay;

    /**
     * 微信公众号openId
     */
    @TableField("wx_public_platform_open_id")
    private String wxPublicPlatformOpenId;

    /**
     * 是否需要请求授权服务通知 0=否 1=是
     */
    @TableField("is_request_wx_notify")
    private Boolean isRequestWxNotify;

    /**
     * 累计充值余额
     */
    @TableField("total_balance")
    private BigDecimal totalBalance;

    /**
     * 累计消费余额
     */
    @TableField("total_consume_balance")
    private BigDecimal totalConsumeBalance;

    /**
     * 累计获得积分
     */
    @TableField("total_points")
    private BigDecimal totalPoints;

    /**
     * 累计消费积分
     */
    @TableField("total_consume_points")
    private BigDecimal totalConsumePoints;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

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
