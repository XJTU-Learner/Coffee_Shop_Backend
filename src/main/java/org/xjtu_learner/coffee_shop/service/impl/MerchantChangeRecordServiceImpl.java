package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.auth.context.AdminContext;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.common.enums.AuditStatus;
import org.xjtu_learner.coffee_shop.common.enums.CertificateType;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.form.AuditChangeForm;
import org.xjtu_learner.coffee_shop.entity.form.MerchantChangeForm;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.po.Merchant;
import org.xjtu_learner.coffee_shop.entity.po.MerchantChangeRecord;
import org.xjtu_learner.coffee_shop.dao.MerchantChangeRecordMapper;
import org.xjtu_learner.coffee_shop.service.IMerchantChangeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xjtu_learner.coffee_shop.service.IMerchantService;
import org.xjtu_learner.coffee_shop.service.IShopService;

import java.time.LocalDateTime;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.*;

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

    private final IMerchantService merchantService;
    private final IShopService shopService;

    public MerchantChangeRecordServiceImpl(MerchantServiceImpl merchantService, IShopService shopService) {
        this.merchantService = merchantService;
        this.shopService = shopService;
    }

    @Override
    public void saveRecord(MerchantChangeForm formDTO) {
        Integer id = MerchantContext.get().getId();

        // 判断是否有正在进行的审核
        boolean exists = lambdaQuery()
                .eq(MerchantChangeRecord::getMerchantId, id)
                .eq(MerchantChangeRecord::getAuditStatus, AuditStatus.ONGOING)
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
    public void saveInitRecord(MerchantChangeForm formDTO) {
        if (!checkInitForm(formDTO))
            throw new CommonException("商户资料初始化表单不完整，商户资料初始化的必填字段有newNickname、newCertificateType、newCertificateImg、" +
                    "newRealName、newIdCard、newOpeningBank和newBankCard", INVALID_ARGUMENT);
        saveRecord(formDTO);
    }

    @Override
    public PageDTO<MerchantChangeRecordDTO> getChangeProfileList(PageQuery pageQuery) {
        Page<MerchantChangeRecord> recordPage = lambdaQuery()
                .eq(MerchantChangeRecord::getAuditStatus, AuditStatus.ONGOING)
                .page(pageQuery.toMpPage(pageQuery.getSortBy(), pageQuery.getIsAsc()));

        return PageDTO.of(recordPage, record -> BeanUtil.copyProperties(record, MerchantChangeRecordDTO.class));
    }

    @Override
    @Transactional
    public void auditChangeProfile(AuditChangeForm form) {
        // 检查该记录是否正在审核中
        MerchantChangeRecord record = lambdaQuery()
                .eq(MerchantChangeRecord::getId, form.getId())
                .eq(MerchantChangeRecord::getAuditStatus, AuditStatus.ONGOING)
                .one();
        if (record == null) throw new CommonException("变更申请记录不存在", NOT_EXIST);
        if (record.getAuditStatus() != AuditStatus.ONGOING)
            throw new CommonException("变更申请记录不在进行中", AUDIT_NOT_ONGOING);

        boolean success = lambdaUpdate()
                .set(MerchantChangeRecord::getAuditor, AdminContext.get().getId())
                .set(form.getSuccess(), MerchantChangeRecord::getAuditStatus, AuditStatus.SUCCEED)
                .set(!form.getSuccess(), MerchantChangeRecord::getAuditStatus, AuditStatus.FAILED)
                .set(!form.getSuccess(), MerchantChangeRecord::getAuditReason, form.getAuditReason())
                .set(MerchantChangeRecord::getAuditTime, LocalDateTime.now())
                .set(MerchantChangeRecord::getUpdateAt, LocalDateTime.now())
                .eq(MerchantChangeRecord::getId, form.getId())
                .update();

        // 审核通过则修改商户信息
        if (success && form.getSuccess()) {
            merchantService.updateMerchant(record);
            // 同时将门店昵称写入到tb_shop表当中
            if (record.getNewNickname() != null) {
                shopService.setShopNickname(record.getMerchantId(), record.getNewNickname());
            }
        }
    }

    private boolean checkInitForm(MerchantChangeForm formDTO) {
        // 检查表单中是否包含商户资料初始化的必填字段：newNickname、newCertificateType、newCertificateImg、newRealName、newIdCard、
        // newOpeningBank和newBankCard
        if (StrUtil.isBlank(formDTO.getNewNickname())) return false;
        if (formDTO.getNewCertificateType() == null) return false;
        if (StrUtil.isBlank(formDTO.getNewCertificateImg())) return false;
        if (StrUtil.isBlank(formDTO.getNewRealName())) return false;
        if (StrUtil.isBlank(formDTO.getNewIdCard())) return false;
        if (StrUtil.isBlank(formDTO.getNewOpeningBank())) return false;
        if (StrUtil.isBlank(formDTO.getNewBankCard())) return false;

        return true;
    }
}
