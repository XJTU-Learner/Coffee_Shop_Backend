package org.xjtu_learner.coffee_shop.controller.merchant;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.entity.dto.MerchantChangeFormDTO;
import org.xjtu_learner.coffee_shop.entity.dto.ShopChangeFormDTO;
import org.xjtu_learner.coffee_shop.entity.vo.ApiResponse;
import org.xjtu_learner.coffee_shop.service.IMerchantChangeRecordService;
import org.xjtu_learner.coffee_shop.service.IMerchantService;
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
    public ApiResponse<String> initProfile(@RequestBody MerchantChangeFormDTO formDTO) {
        merchantChangeRecordService.saveInitRecord(formDTO);
        return ApiResponse.success("初始化资料申请发送成功！");
    }

    @PostMapping("/changeProfile")
    public ApiResponse<String> changeProfile(@RequestBody MerchantChangeFormDTO formDTO) {
        merchantChangeRecordService.saveRecord(formDTO);
        return ApiResponse.success("变更申请发送成功！");
    }

    @PostMapping("/initShop")
    public ApiResponse<String> initShop(@RequestBody ShopChangeFormDTO formDTO) {
        shopChangeRecordService.saveInitRecord(formDTO);
        return ApiResponse.success("初始化门店申请发送成功！");
    }

    @PostMapping("/changeShop")
    public ApiResponse<String> changeShop(@RequestBody ShopChangeFormDTO formDTO) {
        shopChangeRecordService.saveRecord(formDTO);
        return ApiResponse.success("变更申请发送成功！");
    }
}
