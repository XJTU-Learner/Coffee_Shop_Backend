package org.xjtu_learner.coffee_shop.controller.member;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.common.auth.context.MemberContext;
import org.xjtu_learner.coffee_shop.entity.dto.ApiResponse;
import org.xjtu_learner.coffee_shop.entity.dto.CouponsMemberDTO;
import org.xjtu_learner.coffee_shop.entity.po.CouponsMemberRelation;
import org.xjtu_learner.coffee_shop.service.ICouponsMemberRelationService;

import java.util.List;

@RestController
@RequestMapping("/member/coupons")
public class MemberCouponsController {

    private final ICouponsMemberRelationService couponsMemberRelationService;

    public MemberCouponsController(ICouponsMemberRelationService couponsMemberRelationService) {
        this.couponsMemberRelationService = couponsMemberRelationService;
    }

    @GetMapping
    public ApiResponse<List<CouponsMemberDTO>> getCouponsMemberList() {
        Integer memberId = MemberContext.get().getId();

        List<CouponsMemberRelation> relationList =  couponsMemberRelationService.getCouponsMemberRelationList(memberId);
        List<CouponsMemberDTO> couponsMemberDTOList =  couponsMemberRelationService.getCouponsMemberDTOList(relationList);

        return ApiResponse.success(couponsMemberDTOList);
    }
}
