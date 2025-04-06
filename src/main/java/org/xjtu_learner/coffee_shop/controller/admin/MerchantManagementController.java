package org.xjtu_learner.coffee_shop.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.entity.vo.ApiResponse;
import org.xjtu_learner.coffee_shop.entity.vo.MerchantChangeApplication;
import org.xjtu_learner.coffee_shop.service.IMerchantChangeRecordService;

import java.util.List;

@RestController
@RequestMapping("/admin/merchantManagement")
public class MerchantManagementController {

    private final IMerchantChangeRecordService merchantChangeRecordService;

    public MerchantManagementController(IMerchantChangeRecordService merchantChangeRecordService) {
        this.merchantChangeRecordService = merchantChangeRecordService;
    }

    @GetMapping("/initProfile")
    public ApiResponse<List<MerchantChangeApplication>> initProfile(){

//        merchantChangeRecordService.getInitProfileApplication();


        return null;
    }


}
