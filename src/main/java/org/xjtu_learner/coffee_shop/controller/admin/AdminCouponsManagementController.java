package org.xjtu_learner.coffee_shop.controller.admin;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.form.CouponsChangeForm;
import org.xjtu_learner.coffee_shop.entity.form.CouponsForm;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.form.RelationChangeForm;
import org.xjtu_learner.coffee_shop.entity.po.Coupons;
import org.xjtu_learner.coffee_shop.service.ICouponsGoodsRelationService;
import org.xjtu_learner.coffee_shop.service.ICouponsService;
import org.xjtu_learner.coffee_shop.service.ICouponsShopRelationService;

import java.util.List;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.INVALID_ARGUMENT;
import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.NOT_EXIST;

@RestController
@RequestMapping("/admin/coupons")
public class AdminCouponsManagementController {

    private final ICouponsService couponsService;
    private final ICouponsGoodsRelationService couponsGoodsRelationService;
    private final ICouponsShopRelationService couponsShopRelationService;

    public AdminCouponsManagementController(ICouponsService couponsService, ICouponsGoodsRelationService couponsGoodsRelationService, ICouponsShopRelationService couponsShopRelationService) {
        this.couponsService = couponsService;
        this.couponsGoodsRelationService = couponsGoodsRelationService;
        this.couponsShopRelationService = couponsShopRelationService;
    }

    @GetMapping
    public ApiResponse<PageDTO<Coupons>> getCouponsPage(PageQuery pageQuery) {
        PageDTO<Coupons> couponsList = couponsService.getCouponsPage(pageQuery);
        return ApiResponse.success(couponsList);
    }

    @PostMapping
    public ApiResponse<String> createCoupons(@RequestBody CouponsForm form) {
        couponsService.createCoupons(form);
        return ApiResponse.success("创建优惠卷成功！");
    }

    @PutMapping
    @Transactional
    public ApiResponse<String> updateCoupons(@RequestBody CouponsChangeForm form) {

        CouponsForm base = form.getBase();
        if (base == null) throw new CommonException("参数base不允许为空", INVALID_ARGUMENT);
        couponsService.updateCoupons(base);

        Integer couponsId = base.getId();
        RelationChangeForm relatedGoodsChange = form.getRelatedGoodsChange();
        if (!base.getIsGoodsUniversal() && relatedGoodsChange != null) {
            if (relatedGoodsChange.getPlus() != null) {
                couponsGoodsRelationService.createRelations(couponsId, relatedGoodsChange.getPlus());
            }
            if (relatedGoodsChange.getSubtract() != null) {
                couponsGoodsRelationService.deleteRelation(couponsId, relatedGoodsChange.getSubtract());
            }
        }
        RelationChangeForm relatedShopChange = form.getRelatedShopChange();
        if (!base.getIsShopUniversal() || relatedShopChange != null) {
            if (relatedShopChange.getPlus() != null) {
                couponsShopRelationService.createRelations(couponsId, relatedShopChange.getPlus());
            }
            if (relatedShopChange.getSubtract() != null) {
                couponsShopRelationService.deleteRelation(couponsId, relatedShopChange.getSubtract());
            }
        }

        return ApiResponse.success("更新优惠卷成功！");
    }

    @DeleteMapping
    public ApiResponse<String> deleteCoupons(@RequestParam("couponsId") Integer id) {
        couponsService.deleteCoupons(id);
        return ApiResponse.success("删除优惠卷成功！");
    }

    @GetMapping("/relatedGoods")
    public ApiResponse<List<RelationDTO>> getRelatedGoodsList(@RequestParam("couponsId") Integer id) {
        Coupons coupons = couponsService.getCoupons(id);
        if (coupons.getIsGoodsUniversal()) {
            return ApiResponse.failure("该优惠券对商品通用");
        }
        List<RelationDTO> list = couponsGoodsRelationService.getRelatedGoodsList(id);
        if (list.isEmpty()) {
            throw new CommonException("未查询到该优惠券到指定商品信息", NOT_EXIST);
        }
        return ApiResponse.success(list);
    }

    @GetMapping("/relatedShop")
    public ApiResponse<List<RelationDTO>> getRelatedShopList(@RequestParam("couponsId") Integer id) {
        Coupons coupons = couponsService.getCoupons(id);
        if (coupons.getIsShopUniversal()) {
            return ApiResponse.failure("该优惠券对门店通用");
        }
        List<RelationDTO> list = couponsShopRelationService.getRelatedShopList(id);
        if (list.isEmpty()) {
            throw new CommonException("未查询到该优惠券到指定门店信息", NOT_EXIST);
        }
        return ApiResponse.success(list);
    }
}
