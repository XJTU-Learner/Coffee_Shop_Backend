package org.xjtu_learner.coffee_shop.common.auth.interceptor;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.xjtu_learner.coffee_shop.common.auth.context.MemberContext;
import org.xjtu_learner.coffee_shop.common.auth.session.impl.MemberSessionManager;
import org.xjtu_learner.coffee_shop.entity.dto.MemberDTO;


@Component
public class MemberRefreshTokenInterceptor implements HandlerInterceptor {

    private final MemberSessionManager sessionManager;

    public MemberRefreshTokenInterceptor(MemberSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception {

        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        MemberDTO dto = sessionManager.getSessionAndRefresh(token);

        if (dto == null) return true;

        MemberContext.save(dto);
        return true;
    }

    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex) throws Exception {
        MemberContext.remove();
    }
}
