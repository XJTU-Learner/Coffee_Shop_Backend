package org.xjtu_learner.coffee_shop.common.auth.interceptor;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.common.auth.session.impl.MerchantSessionManager;
import org.xjtu_learner.coffee_shop.entity.dto.MerchantDTO;


@Component
public class MerchantRefreshTokenInterceptor implements HandlerInterceptor {

    private final MerchantSessionManager sessionManager;

    public MerchantRefreshTokenInterceptor(MerchantSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception {

        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        MerchantDTO dto = sessionManager.getSessionAndRefresh(token);

        if (dto == null) return true;

        MerchantContext.save(dto);
        return true;
    }

    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex) throws Exception {
        MerchantContext.remove();
    }
}
