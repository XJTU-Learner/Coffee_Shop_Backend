package org.xjtu_learner.coffee_shop.service.impl;

import org.bouncycastle.oer.Switch;
import org.springframework.beans.factory.annotation.Autowired;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.entity.dto.MemberBillDTO;
import org.xjtu_learner.coffee_shop.entity.dto.WithdrawalForm;
import org.xjtu_learner.coffee_shop.entity.po.Merchant;
import org.xjtu_learner.coffee_shop.entity.po.MerchantBillingRecord;
import org.xjtu_learner.coffee_shop.dao.MerchantBillingRecordMapper;
import org.xjtu_learner.coffee_shop.entity.po.MerchantWithdrawRecord;
import org.xjtu_learner.coffee_shop.service.IMerchantBillingRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商家账单记录表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class MerchantBillingRecordServiceImpl extends ServiceImpl<MerchantBillingRecordMapper, MerchantBillingRecord> implements IMerchantBillingRecordService {


    @Autowired
    private MerchantBillingRecordMapper merchantBillingRecordMapper;

    //获取近某个时间段的用户下单记录

    public List<MemberBillDTO> get_memberbill(String timeRange){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now;
        switch (timeRange.toLowerCase()) {
            case "week":
                startTime = now.minus(1, ChronoUnit.WEEKS);
                break;
            case "month":
                startTime = now.minus(1, ChronoUnit.MONTHS);
                break;
            case "year":
                startTime = now.minus(1, ChronoUnit.YEARS);
                break;
            default:
                // 默认查询近一星期的记录
                startTime = now.minus(1, ChronoUnit.WEEKS);
                break;
        }
        List<MerchantBillingRecord> list= merchantBillingRecordMapper.selectRecordsAfter(startTime);
        System.out.println(list.size());
        List<MemberBillDTO> result = new ArrayList<>();

        for(MerchantBillingRecord record:list){
            MemberBillDTO memberBillDTO = new MemberBillDTO();
            memberBillDTO.setOrderId(String.valueOf(record.getId()));
            memberBillDTO.setOrderTime(record.getCreateAt().toString());
            memberBillDTO.setOrderPrice(record.getNumber().toString());
            result.add(memberBillDTO);
        }

        return result;


    }

}
