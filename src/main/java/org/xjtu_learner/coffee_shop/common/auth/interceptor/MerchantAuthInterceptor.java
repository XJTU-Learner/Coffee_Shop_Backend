package org.xjtu_learner.coffee_shop.common.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.entity.dto.MerchantDTO;

@Component
public class MerchantAuthInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MerchantDTO user = MerchantContext.get();
        if (user == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}
