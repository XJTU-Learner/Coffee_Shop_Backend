package org.xjtu_learner.coffee_shop.controller.admin;

import org.springframework.web.bind.annotation.*;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.service.IMerchantChangeRecordService;
import org.xjtu_learner.coffee_shop.service.IShopChangeRecordService;

@RestController
@RequestMapping("/admin/merchantManagement")
public class MerchantManagementController {

    private final IMerchantChangeRecordService merchantChangeRecordService;
    private final IShopChangeRecordService shopChangeRecordService;

    public MerchantManagementController(IMerchantChangeRecordService merchantChangeRecordService, IShopChangeRecordService shopChangeRecordService) {
        this.merchantChangeRecordService = merchantChangeRecordService;
        this.shopChangeRecordService = shopChangeRecordService;
    }

    @GetMapping("/merchantProfile")
    public ApiResponse<PageDTO<MerchantChangeRecordDTO>> getChangeProfileList(PageQuery pageQuery) {

        PageDTO<MerchantChangeRecordDTO> changeProfilePage = merchantChangeRecordService.getChangeProfileList(pageQuery);
        return ApiResponse.success(changeProfilePage);
    }


    @GetMapping("/shopProfile")
    public ApiResponse<PageDTO<ShopChangeRecordDTO>> getChangeShopList(PageQuery pageQuery){

        PageDTO<ShopChangeRecordDTO> changeShopList = shopChangeRecordService.getChangeShopList(pageQuery);
        return ApiResponse.success(changeShopList);
    }

    @PostMapping("/merchantProfile")
    public ApiResponse<String> auditChangeProfile(@RequestBody AuditChangeForm form){
        merchantChangeRecordService.auditChangeProfile(form);
        return ApiResponse.success("审核成功！");
    }

    @PostMapping("/shopProfile")
    public ApiResponse<String> auditChangeShop(@RequestBody AuditChangeForm form){
        shopChangeRecordService.auditChangeShop(form);
        return ApiResponse.success("审核成功！");

    }

}
