package org.xjtu_learner.coffee_shop.controller.member;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.web.bind.annotation.*;
import org.xjtu_learner.coffee_shop.common.auth.context.MemberContext;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.form.NearbySearchForm;
import org.xjtu_learner.coffee_shop.entity.po.CouponsMemberRelation;
import org.xjtu_learner.coffee_shop.entity.po.Shop;
import org.xjtu_learner.coffee_shop.entity.po.ShopGoodsRelation;
import org.xjtu_learner.coffee_shop.service.ICouponsMemberRelationService;
import org.xjtu_learner.coffee_shop.service.IShopGoodsRelationService;
import org.xjtu_learner.coffee_shop.service.IShopService;

import java.util.List;
import java.util.Map;


@RestController()
@RequestMapping("/member/query")
public class MemberQueryController {

    private final IShopService shopService;
    private final IShopGoodsRelationService shopGoodsRelationService;

    public MemberQueryController(IShopService shopService, IShopGoodsRelationService shopGoodsRelationService) {
        this.shopService = shopService;
        this.shopGoodsRelationService = shopGoodsRelationService;
    }

    @GetMapping("/nearbyShops")
    public ApiResponse<List<NearbySearchDTO<ShopDTO>>> getNearbyShopList(@RequestBody NearbySearchForm form) {

        Map<Shop, String> nearbyShop = shopService.getNearbyShop(form);

        List<NearbySearchDTO<ShopDTO>> nearbyShopDTO = nearbyShop.entrySet().stream()
                .map((entry) -> (NearbySearchDTO.<ShopDTO>builder()
                        .dto(BeanUtil.copyProperties(entry.getKey(), ShopDTO.class))
                        .distance(entry.getValue())
                        .build()))
                .toList();

        return ApiResponse.success(nearbyShopDTO);
    }

    @GetMapping("/shopGoods")
    public ApiResponse<List<ShopGoodsDTO>> getShopGoodsList(@RequestParam("shopId") Integer shopId) {

        List<ShopGoodsRelation> relationList = shopGoodsRelationService.getShopGoodsRelationList(shopId);
        List<ShopGoodsDTO> shopGoodsDTOList = shopGoodsRelationService.getShopGoodsDTOList(relationList);

        return ApiResponse.success(shopGoodsDTOList);
    }
}
