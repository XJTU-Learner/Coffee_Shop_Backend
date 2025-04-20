package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.xjtu_learner.coffee_shop.common.auth.context.AdminContext;
import org.xjtu_learner.coffee_shop.common.auth.context.MerchantContext;
import org.xjtu_learner.coffee_shop.common.enums.AuditStatus;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.*;
import org.xjtu_learner.coffee_shop.entity.form.AuditChangeForm;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.form.ShopChangeForm;
import org.xjtu_learner.coffee_shop.entity.po.MerchantChangeRecord;
import org.xjtu_learner.coffee_shop.entity.po.Shop;
import org.xjtu_learner.coffee_shop.entity.po.ShopChangeRecord;
import org.xjtu_learner.coffee_shop.dao.ShopChangeRecordMapper;
import org.xjtu_learner.coffee_shop.service.IShopChangeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xjtu_learner.coffee_shop.service.IShopService;

import java.time.LocalDateTime;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.*;
import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.AUDIT_NOT_ONGOING;

/**
 * <p>
 * 门店重要信息变更记录表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-06
 */
@Service
public class ShopChangeRecordServiceImpl extends ServiceImpl<ShopChangeRecordMapper, ShopChangeRecord> implements IShopChangeRecordService {

    private final IShopService shopService;

    public ShopChangeRecordServiceImpl(IShopService shopService) {
        this.shopService = shopService;
    }

    @Override
    public void saveInitRecord(ShopChangeForm formDTO) {
        if (!checkInitForm(formDTO)) throw new CommonException(
                "商铺资料初始化表单不完整，门店资料初始化的必填字段：newNickname、" +
                        "newProvince、newCity、newArea、newStreet、newHouseNumber、newContactRealname、newContactPhone、" +
                        "newBusinessLicense、newOpenTime、newCloseTime、newLongitude和newLatitude\n", INVALID_ARGUMENT);


        saveRecord(formDTO);
    }

    @Override
    public void saveRecord(ShopChangeForm formDTO) {
        Integer id = MerchantContext.get().getId();

        // 判断是否有正在进行的审核
        boolean exists = lambdaQuery()
                .eq(ShopChangeRecord::getMerchantId, id)
                .eq(ShopChangeRecord::getAuditStatus,AuditStatus.ONGOING)
                .exists();
        if (exists) {
            throw new CommonException("已有正在进行的审核", AUDIT_ONGOING);
        }

        Shop shop = shopService.lambdaQuery().eq(Shop::getId, id).one();

        ShopChangeRecord record = BeanUtil.copyProperties(formDTO, ShopChangeRecord.class);
        BeanUtil.copyProperties(shop, record, "id");

        record.setMerchantId(id);
        save(record);
    }

    @Override
    public PageDTO<ShopChangeRecordDTO> getChangeShopList(PageQuery pageQuery) {
        Page<ShopChangeRecord> recordPage = lambdaQuery()
                .eq(ShopChangeRecord::getAuditStatus, AuditStatus.ONGOING)
                .page(pageQuery.toMpPage(pageQuery.getSortBy(), pageQuery.getIsAsc()));

        return PageDTO.of(recordPage, record -> BeanUtil.copyProperties(record, ShopChangeRecordDTO.class));
    }

    @Override
    public void auditChangeShop(AuditChangeForm form) {
        // 检查该记录是否正在审核中
        ShopChangeRecord record = lambdaQuery()
                .eq(ShopChangeRecord::getId, form.getId())
                .eq(ShopChangeRecord::getAuditStatus,AuditStatus.ONGOING)
                .one();
        if (record == null) throw new CommonException("变更申请记录不存在", NOT_EXIST);
        if (record.getAuditStatus() != AuditStatus.ONGOING)
            throw new CommonException("变更申请记录不在进行中", AUDIT_NOT_ONGOING);

        boolean success = lambdaUpdate()
                .set(ShopChangeRecord::getAuditor, AdminContext.get().getId())
                .set(form.getSuccess(), ShopChangeRecord::getAuditStatus, AuditStatus.SUCCEED)
                .set(!form.getSuccess(), ShopChangeRecord::getAuditStatus, AuditStatus.FAILED)
                .set(!form.getSuccess(), ShopChangeRecord::getAuditReason, form.getAuditReason())
                .set(ShopChangeRecord::getAuditTime, LocalDateTime.now())
                .set(ShopChangeRecord::getUpdateAt, LocalDateTime.now())
                .eq(ShopChangeRecord::getId, form.getId())
                .update();

        // 如果审批通过则修改门店信息
        if (success && form.getSuccess()) {
            shopService.updateShop(record);
        }
    }

    private boolean checkInitForm(ShopChangeForm shopChangeForm) {
        // 检查表单中是否包含门店资料初始化的必填字段：newProvince、newCity、newArea、newStreet、newHouseNumber、
        // newContactRealname、newContactPhone、newBusinessLicense、newOpenTime、newCloseTime、newLongitude和newLatitude
        if (StrUtil.isBlank(shopChangeForm.getNewProvince())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewCity())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewArea())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewStreet())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewHouseNumber())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewContactRealname())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewContactPhone())) return false;
        if (StrUtil.isBlank(shopChangeForm.getNewBusinessLicense())) return false;
        if (shopChangeForm.getNewOpenTime() == null) return false;
        if (shopChangeForm.getNewCloseTime() == null) return false;
        if (shopChangeForm.getNewLongitude() == null) return false;
        if (shopChangeForm.getNewLatitude() == null) return false;

        return true;
    }
}
