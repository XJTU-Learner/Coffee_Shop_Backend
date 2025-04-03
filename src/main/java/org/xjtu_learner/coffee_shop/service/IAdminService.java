package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.LoginFormDTO;
import org.xjtu_learner.coffee_shop.entity.po.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import org.xjtu_learner.coffee_shop.entity.vo.ApiResponse;

/**
 * <p>
 * 后台管理员表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface IAdminService extends IService<Admin> {

    ApiResponse<String> login(LoginFormDTO loginForm);

    ApiResponse<String> loginByMobile(LoginFormDTO loginForm);

    ApiResponse<String> logout();
}
