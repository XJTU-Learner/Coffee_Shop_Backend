package org.xjtu_learner.coffee_shop.controller.merchant;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xjtu_learner.coffee_shop.entity.dto.ApiResponse;
import org.xjtu_learner.coffee_shop.entity.dto.MemberBillDTO;
import org.xjtu_learner.coffee_shop.entity.dto.WithdrawalForm;
import org.xjtu_learner.coffee_shop.entity.po.MerchantWithdrawRecord;
import org.xjtu_learner.coffee_shop.service.impl.MerchantBillingRecordServiceImpl;
import org.xjtu_learner.coffee_shop.service.impl.MerchantWithdrawRecordServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/merchant/bill")
public class MerchantBillController {

    private  final MerchantBillingRecordServiceImpl  merchantBillingRecordService;
    private final MerchantWithdrawRecordServiceImpl merchantWithdrawRecordService;

    public MerchantBillController(MerchantBillingRecordServiceImpl merchantBillingRecordService, MerchantWithdrawRecordServiceImpl merchantWithdrawRecordService) {
        this.merchantBillingRecordService = merchantBillingRecordService;
        this.merchantWithdrawRecordService = merchantWithdrawRecordService;
    }


    @RequestMapping("/get_withdrawllist")
    //获取全部提现记录
    public ApiResponse<List<MerchantWithdrawRecord>> get_withdrawllist(){
        return ApiResponse.success(merchantWithdrawRecordService.getAllWithdrawalRecord());
    }

    @RequestMapping("/apply_withdrawal")
    //商家发起提现申请
    public ApiResponse<String> apply_withdrawal(@RequestBody WithdrawalForm form){

        boolean result = merchantWithdrawRecordService.submitWithdrawalRecord(form);
        if(!result){
            return ApiResponse.failure("提现金额不足！");
        }
        return  ApiResponse.success("提现申请已提交！");
    }

    @RequestMapping("/get_memberbill")
    //查看一段时间内用户下单记录
    public ApiResponse<List<MemberBillDTO>> get_memberbill(@RequestParam("timeRange")  String timeRange){
        return ApiResponse.success(merchantBillingRecordService.get_memberbill(timeRange));
    }



}
