package org.xjtu_learner.coffee_shop.service.impl;

import org.xjtu_learner.coffee_shop.entity.po.Admin;
import org.xjtu_learner.coffee_shop.dao.AdminMapper;
import org.xjtu_learner.coffee_shop.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台管理员表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

}
