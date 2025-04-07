package org.xjtu_learner.coffee_shop.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.entity.dto.ApiResponse;
import org.xjtu_learner.coffee_shop.entity.dto.PageDTO;
import org.xjtu_learner.coffee_shop.entity.dto.PageQuery;
import org.xjtu_learner.coffee_shop.entity.dto.RequireChangeItem;
import org.xjtu_learner.coffee_shop.service.IMerchantChangeRecordService;
import org.xjtu_learner.coffee_shop.service.IShopChangeRecordService;

import java.util.List;

@RestController
@RequestMapping("/admin/merchantManagement")
public class MerchantManagementController {

    private final IMerchantChangeRecordService merchantChangeRecordService;
    private final IShopChangeRecordService shopChangeRecordService;

    public MerchantManagementController(IMerchantChangeRecordService merchantChangeRecordService, IShopChangeRecordService shopChangeRecordService) {
        this.merchantChangeRecordService = merchantChangeRecordService;
        this.shopChangeRecordService = shopChangeRecordService;
    }

    @GetMapping("/changeProfile")
    public ApiResponse<PageDTO<RequireChangeItem>> changeProfile(PageQuery pageQuery) {

        PageDTO<RequireChangeItem> changeProfilePage = merchantChangeRecordService.getChangeProfileList(pageQuery);
        return ApiResponse.success(changeProfilePage);
    }


    @GetMapping("/changeShop")
    public ApiResponse<PageDTO<RequireChangeItem>> changeShop(PageQuery pageQuery){

        PageDTO<RequireChangeItem> changeShopList = shopChangeRecordService.getChangeShopList(pageQuery);
        return ApiResponse.success(changeShopList);
    }

}
