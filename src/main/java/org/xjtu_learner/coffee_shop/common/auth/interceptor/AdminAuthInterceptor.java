package org.xjtu_learner.coffee_shop.common.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.xjtu_learner.coffee_shop.common.auth.context.AdminContext;
import org.xjtu_learner.coffee_shop.entity.dto.AdminDTO;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception {
        AdminDTO user = AdminContext.get();
        if (user == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
