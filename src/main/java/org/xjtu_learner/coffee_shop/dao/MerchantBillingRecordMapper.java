package org.xjtu_learner.coffee_shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.xjtu_learner.coffee_shop.entity.dto.MemberBillDTO;
import org.xjtu_learner.coffee_shop.entity.po.MerchantBillingRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商家账单记录表 Mapper 接口
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface MerchantBillingRecordMapper extends BaseMapper<MerchantBillingRecord> {

    @Select("SELECT * FROM tb_merchant_billing_record WHERE create_at >= #{startTime} AND type=1")
    List<MerchantBillingRecord> selectRecordsAfter(LocalDateTime startTime);
}

