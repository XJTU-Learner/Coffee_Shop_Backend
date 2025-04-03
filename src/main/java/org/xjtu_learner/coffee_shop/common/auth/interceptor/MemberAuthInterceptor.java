package org.xjtu_learner.coffee_shop.common.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.xjtu_learner.coffee_shop.common.auth.context.MemberContext;
import org.xjtu_learner.coffee_shop.entity.dto.MemberDTO;

@Component
public class MemberAuthInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MemberDTO user = MemberContext.get();
        if (user == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
