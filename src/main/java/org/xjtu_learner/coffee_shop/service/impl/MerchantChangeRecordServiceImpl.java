package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.common.enums.CertificateType;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.MerchantChangeFormDTO;
import org.xjtu_learner.coffee_shop.entity.po.Merchant;
import org.xjtu_learner.coffee_shop.entity.po.MerchantChangeRecord;
import org.xjtu_learner.coffee_shop.dao.MerchantChangeRecordMapper;
import org.xjtu_learner.coffee_shop.entity.po.ShopChangeRecord;
import org.xjtu_learner.coffee_shop.service.IMerchantChangeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.AUDIT_ONGOING;
import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.INVALID_ARGUMENT;

/**
 * <p>
 * 商户重要信息变更表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-06
 */
@Service
public class MerchantChangeRecordServiceImpl extends ServiceImpl<MerchantChangeRecordMapper, MerchantChangeRecord> implements IMerchantChangeRecordService {

    private final MerchantServiceImpl merchantService;

    public MerchantChangeRecordServiceImpl(MerchantServiceImpl merchantService) {
        this.merchantService = merchantService;
    }

    @Override
    public void saveRecord(MerchantChangeFormDTO formDTO) {
        Integer id = MerchantContext.get().getId();

        // 判断是否有正在进行的审核
        boolean exists = lambdaQuery()
                .eq(MerchantChangeRecord::getMerchantId, id)
                .exists();
        if (exists) {
            throw new CommonException("已有正在进行的审核", AUDIT_ONGOING);
        }


        // 查询出当前商户的信息并拷贝到MerchantChangeRecord当中
        Merchant merchant = merchantService.lambdaQuery().eq(Merchant::getId, id).one();
        MerchantChangeRecord record = BeanUtil.copyProperties(merchant, MerchantChangeRecord.class, "id");

        //将变更后信息拷贝到MerchantChangeRecord当中
        BeanUtil.copyProperties(formDTO, record, "newCertificateType");
        if (formDTO.getNewCertificateType() != null) {
            record.setNewCertificateType(CertificateType.of(formDTO.getNewCertificateType()));
        }
        record.setMerchantId(id);
        save(record);
    }

    @Override
    public void saveInitRecord(MerchantChangeFormDTO formDTO) {
        if (!checkInitForm(formDTO))
            throw new CommonException("商户资料初始化表单不完整，商户资料初始化的必填字段有newCertificateType、newCertificateImg、" +
                    "newRealName、newIdCard、newOpeningBank和newBankCard", INVALID_ARGUMENT);
        saveRecord(formDTO);
    }

    private boolean checkInitForm(MerchantChangeFormDTO formDTO) {
        // 检查表单中是否包含商户资料初始化的必填字段：newCertificateType、newCertificateImg、newRealName、newIdCard、
        // newOpeningBank和newBankCard
        if (formDTO.getNewCertificateType() == null) return false;
        if (StrUtil.isBlank(formDTO.getNewCertificateImg())) return false;
        if (StrUtil.isBlank(formDTO.getNewRealName())) return false;
        if (StrUtil.isBlank(formDTO.getNewIdCard())) return false;
        if (StrUtil.isBlank(formDTO.getNewOpeningBank())) return false;
        if (StrUtil.isBlank(formDTO.getNewBankCard())) return false;

        return true;
    }
}
