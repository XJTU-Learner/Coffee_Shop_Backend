package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.dto.CouponsMemberDTO;
import org.xjtu_learner.coffee_shop.entity.po.CouponsMemberRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 优惠卷用户关联表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface ICouponsMemberRelationService extends IService<CouponsMemberRelation> {

    List<CouponsMemberRelation> getCouponsMemberRelationList(Integer memberId);

    List<CouponsMemberDTO> getCouponsMemberDTOList(List<CouponsMemberRelation> relationList);

    Integer checkValid(Integer memberId, Integer couponsMemberRelationId);
}
