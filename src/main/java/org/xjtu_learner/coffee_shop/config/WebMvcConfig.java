package org.xjtu_learner.coffee_shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.xjtu_learner.coffee_shop.common.auth.interceptor.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final AdminRefreshTokenInterceptor adminRefreshTokenInterceptor;

    private final MemberAuthInterceptor memberAuthInterceptor;
    private final MemberRefreshTokenInterceptor memberRefreshTokenInterceptor;

    private final MerchantAuthInterceptor merchantAuthInterceptor;
    private final MerchantRefreshTokenInterceptor merchantRefreshTokenInterceptor;

    public WebMvcConfig(AdminAuthInterceptor adminAuthInterceptor, AdminRefreshTokenInterceptor adminRefreshTokenInterceptor, MemberAuthInterceptor memberAuthInterceptor, MemberRefreshTokenInterceptor memberRefreshTokenInterceptor, MerchantAuthInterceptor merchantAuthInterceptor, MerchantRefreshTokenInterceptor merchantRefreshTokenInterceptor) {
        this.adminAuthInterceptor = adminAuthInterceptor;
        this.adminRefreshTokenInterceptor = adminRefreshTokenInterceptor;
        this.memberAuthInterceptor = memberAuthInterceptor;
        this.memberRefreshTokenInterceptor = memberRefreshTokenInterceptor;
        this.merchantAuthInterceptor = merchantAuthInterceptor;
        this.merchantRefreshTokenInterceptor = merchantRefreshTokenInterceptor;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册管理员拦截器
        registry.addInterceptor(adminRefreshTokenInterceptor).order(0);
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/auth/login")
                .excludePathPatterns("/admin/auth/signup")
                .excludePathPatterns("/admin/auth/code")
                .excludePathPatterns("/admin/auth/loginByMobile").order(1);

        //注册用户拦截器
        registry.addInterceptor(memberRefreshTokenInterceptor).order(0);
        registry.addInterceptor(memberAuthInterceptor)
                .addPathPatterns("/member/**")
                .excludePathPatterns("/member/auth/code")
                .excludePathPatterns("/member/auth/loginByMobile").order(1);


        //商户拦截器
        registry.addInterceptor(merchantRefreshTokenInterceptor).order(0);
        registry.addInterceptor(merchantAuthInterceptor)
                .addPathPatterns("/merchant/**")
                .excludePathPatterns("/merchant/auth/login")
                .excludePathPatterns("/merchant/auth/signup")
                .excludePathPatterns("/merchant/auth/code")
                .excludePathPatterns("/merchant/auth/loginByMobile").order(1);
    }
}
