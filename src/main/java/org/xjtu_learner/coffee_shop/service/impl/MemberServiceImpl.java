package org.xjtu_learner.coffee_shop.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.core.util.RandomUtil;
import me.chanjar.weixin.common.error.WxErrorException;
import org.xjtu_learner.coffee_shop.common.auth.VerificationCodeManager;
import org.xjtu_learner.coffee_shop.common.auth.session.impl.MemberSessionManager;
import org.xjtu_learner.coffee_shop.common.enums.MemberRegisterWay;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.common.utils.HttpContext;
import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.po.Member;
import org.xjtu_learner.coffee_shop.dao.MemberMapper;
import org.xjtu_learner.coffee_shop.service.IMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.*;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.MEMBER_SESSION_CODE_PREFIX;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {

    private final VerificationCodeManager verificationCodeManager;
    private final MemberSessionManager memberSessionManager;
    private final WxMaService wxMaService;

    public MemberServiceImpl(VerificationCodeManager verificationCodeManager, MemberSessionManager memberSessionManager, WxMaService wxMaService) {
        this.verificationCodeManager = verificationCodeManager;
        this.memberSessionManager = memberSessionManager;
        this.wxMaService = wxMaService;
    }

    @Override
    public void sendCode(String mobile) {
        verificationCodeManager.sendCode(MEMBER_SESSION_CODE_PREFIX, mobile);
    }

    @Override
    public String loginByMobile(LoginFormDTO loginForm) {
        // 校验验证码
        if (!verificationCodeManager.verifyCode(MEMBER_SESSION_CODE_PREFIX, loginForm.getMobile(), loginForm.getCode()))
            throw new CommonException("验证码错误", WRONG_VERIFICATION_CODE);

        // 查询用户
        Member member = lambdaQuery()
                .eq(Member::getMobile, loginForm.getMobile())
                .one();

        // 未查询到则注册新用户
        if (member == null) {
            throw new CommonException("用户不存在",ACCOUNT_NOT_EXIST);
        }

        // 为用户在redis创建session
        return memberSessionManager.createSession(member);
    }

    @Override
    public void logout() {
        String token = HttpContext.getRequestHeader("Authorization");
        memberSessionManager.removeSession(token);
    }

    @Override
    public String wxLogin(String code) {
        // 通过code获取openId
        try {
            String openid = wxMaService.getUserService().getSessionInfo(code).getOpenid();
            String phoneNumber = wxMaService.getUserService().getPhoneNumber(code).getPurePhoneNumber();

            // 根据openid查询数据
            Member member = lambdaQuery()
                    .eq(Member::getOpenId, openid)
                    .one();

            if (member == null) {
                member = new Member();
                member.setNickname("用户" + RandomUtil.randomString(6));
                member.setOpenId(openid);
                member.setRegisterTime(LocalDateTime.now());
                member.setMobile(phoneNumber);
                save(member);
            }

            return memberSessionManager.createSession(member);
        } catch (WxErrorException e) {
            throw new CommonException("登陆异常",WX_LOGIN_ERROR);
        }
    }
}
