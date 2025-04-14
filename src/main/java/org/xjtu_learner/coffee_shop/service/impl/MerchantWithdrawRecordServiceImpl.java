package org.xjtu_learner.coffee_shop.service.impl;

import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.entity.dto.WithdrawalForm;
import org.xjtu_learner.coffee_shop.entity.po.Merchant;
import org.xjtu_learner.coffee_shop.entity.po.MerchantWithdrawRecord;
import org.xjtu_learner.coffee_shop.dao.MerchantWithdrawRecordMapper;
import org.xjtu_learner.coffee_shop.service.IMerchantWithdrawRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 商家提现记录表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class MerchantWithdrawRecordServiceImpl extends ServiceImpl<MerchantWithdrawRecordMapper, MerchantWithdrawRecord> implements IMerchantWithdrawRecordService {

    private final MerchantServiceImpl merchantService;

    public MerchantWithdrawRecordServiceImpl(MerchantServiceImpl merchantService) {
        this.merchantService = merchantService;
    }

    //获取全部提现记录
    public List<MerchantWithdrawRecord> getAllWithdrawalRecord(){
        List<MerchantWithdrawRecord> list = lambdaQuery().eq(MerchantWithdrawRecord::getMerchantId,MerchantContext.get().getId()).list();
        return list;
    }

    //提交提现申请
    public boolean submitWithdrawalRecord(WithdrawalForm withdrawalForm){

        Merchant merchant = merchantService.getById(MerchantContext.get().getId());
        BigDecimal temp =BigDecimal.valueOf(withdrawalForm.getAmount()*(-1));
        if(merchant.getWithdrawableBalance().add(temp).compareTo(BigDecimal.valueOf(0))<0){
            return false;
        }
        else {
            MerchantWithdrawRecord merchantWithdrawRecord = new MerchantWithdrawRecord();
            merchantWithdrawRecord.setMerchantId(MerchantContext.get().getId());
            merchantWithdrawRecord.setWithdrawAmount(BigDecimal.valueOf(withdrawalForm.getAmount()));
            merchantWithdrawRecord.setPaymentMode(withdrawalForm.getPayment_method());
            merchantWithdrawRecord.setPlatformFee(BigDecimal.valueOf(withdrawalForm.getAmount() * 0.005));
            merchantWithdrawRecord.setActualAmount(BigDecimal.valueOf(withdrawalForm.getAmount() - withdrawalForm.getAmount() * 0.005));
            merchantWithdrawRecord.setAuditStatus(1);
            switch (merchantWithdrawRecord.getPaymentMode()) {
                case 1:
                    merchantWithdrawRecord.setWechatAccount(merchant.getWechatAccount());
                    break;
                case 2:
                    merchantWithdrawRecord.setAlipayAccount(merchant.getAlipayAccount());
                    break;
                case 3:
                    merchantWithdrawRecord.setBankCard(merchant.getBankCard());
                    merchantWithdrawRecord.setOpeningBankAddress(merchant.getOpeningBank());
                    merchantWithdrawRecord.setOpeningBankName(merchant.getOpeningBank());
                    break;
                default:
                    break;
            }

            save(merchantWithdrawRecord);
            return true;
        }

    }

}
