package org.xjtu_learner.coffee_shop.controller.member;

import org.springframework.web.bind.annotation.*;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.po.ShopGoodsRelation;
import org.xjtu_learner.coffee_shop.service.IShopGoodsRelationService;
import org.xjtu_learner.coffee_shop.service.IShopService;

import java.util.List;


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
    public ApiResponse<PageDTO<ShopDTO>> getNearbyShops(@RequestBody PageQuery pageQuery) {
        return null;
    }

    @GetMapping("/shopGoods")
    public ApiResponse<List<ShopGoodsDTO>> getShopGoodsList(@RequestParam("shopId") Integer shopId) {

        List<ShopGoodsRelation> list = shopGoodsRelationService.getShopGoodsList(shopId);
        List<ShopGoodsDTO> shopGoodsDTOList = shopGoodsRelationService.getShopGoodsDTOList(list);

        return ApiResponse.success(shopGoodsDTOList);
    }
}
