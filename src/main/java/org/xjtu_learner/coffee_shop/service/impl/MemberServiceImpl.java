package org.xjtu_learner.coffee_shop.service.impl;

import org.xjtu_learner.coffee_shop.entity.po.Member;
import org.xjtu_learner.coffee_shop.dao.MemberMapper;
import org.xjtu_learner.coffee_shop.service.IMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {

}
