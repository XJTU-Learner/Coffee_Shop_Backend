package org.xjtu_learner.coffee_shop.controller.merchant;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.entity.dto.MerchantChangeForm;
import org.xjtu_learner.coffee_shop.entity.dto.ShopChangeForm;
import org.xjtu_learner.coffee_shop.entity.dto.ApiResponse;
import org.xjtu_learner.coffee_shop.service.IMerchantChangeRecordService;
import org.xjtu_learner.coffee_shop.service.IShopChangeRecordService;


@RestController
@RequestMapping("/merchant/")
public class MerchantProfileController {

    private final IMerchantChangeRecordService merchantChangeRecordService;
    private final IShopChangeRecordService shopChangeRecordService;

    public MerchantProfileController(IMerchantChangeRecordService merchantChangeRecordService, IShopChangeRecordService shopChangeRecordService) {
        this.merchantChangeRecordService = merchantChangeRecordService;
        this.shopChangeRecordService = shopChangeRecordService;
    }


    @PostMapping("/initProfile")
    @Transactional
    public ApiResponse<String> initProfile(@RequestBody MerchantChangeForm form) {
        merchantChangeRecordService.saveInitRecord(form);
        return ApiResponse.success("初始化资料申请发送成功！");
    }

    @PostMapping("/changeProfile")
    public ApiResponse<String> changeProfile(@RequestBody MerchantChangeForm form) {
        merchantChangeRecordService.saveRecord(form);
        return ApiResponse.success("变更申请发送成功！");
    }

    @PostMapping("/initShop")
    public ApiResponse<String> initShop(@RequestBody ShopChangeForm form) {
        shopChangeRecordService.saveInitRecord(form);
        return ApiResponse.success("初始化门店申请发送成功！");
    }

    @PostMapping("/changeShop")
    public ApiResponse<String> changeShop(@RequestBody ShopChangeForm form) {
        shopChangeRecordService.saveRecord(form);
        return ApiResponse.success("变更申请发送成功！");
    }
}
