package org.xjtu_learner.coffee_shop.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.vo.ApiResponse;
import org.xjtu_learner.coffee_shop.service.IAdminService;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final IAdminService adminService;

    public AdminAuthController(IAdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/admin/login")
    public ApiResponse<String> login(LoginFormDTO loginForm){
        return adminService.login(loginForm);
    }

    @PostMapping("/admin/loginByMobile")
    public ApiResponse<String> loginByMobile(LoginFormDTO loginForm){
        return adminService.loginByMobile(loginForm);
    }

    @GetMapping("/admin/logout")
    public ApiResponse<String> logout(){
        return adminService.logout();


    }


}
