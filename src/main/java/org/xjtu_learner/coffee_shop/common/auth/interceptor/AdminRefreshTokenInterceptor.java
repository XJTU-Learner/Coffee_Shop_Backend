package org.xjtu_learner.coffee_shop.common.auth.interceptor;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.xjtu_learner.coffee_shop.common.auth.context.AdminContext;
import org.xjtu_learner.coffee_shop.common.auth.session.impl.AdminSessionManager;
import org.xjtu_learner.coffee_shop.entity.dto.AdminDTO;


@Component
public class AdminRefreshTokenInterceptor implements HandlerInterceptor {

    private final AdminSessionManager sessionManager;

    public AdminRefreshTokenInterceptor(AdminSessionManager adminSessionManager) {
        this.sessionManager = adminSessionManager;
    }


    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception {

        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        AdminDTO dto = sessionManager.getSessionAndRefresh(token);

        if (dto == null) return true;

        AdminContext.save(dto);
        return true;
    }

    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex) throws Exception {
        AdminContext.remove();
    }
}
