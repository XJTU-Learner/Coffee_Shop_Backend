package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.RelationDTO;
import org.xjtu_learner.coffee_shop.entity.po.CouponsGoodsRelation;
import org.xjtu_learner.coffee_shop.entity.po.CouponsShopRelation;
import org.xjtu_learner.coffee_shop.dao.CouponsShopRelationMapper;
import org.xjtu_learner.coffee_shop.entity.po.Shop;
import org.xjtu_learner.coffee_shop.service.ICouponsShopRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xjtu_learner.coffee_shop.service.IShopService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.INVALID_ARGUMENT;

/**
 * <p>
 * 优惠卷使用店铺范围，优惠卷和店铺关系表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class CouponsShopRelationServiceImpl extends ServiceImpl<CouponsShopRelationMapper, CouponsShopRelation> implements ICouponsShopRelationService {

    private final IShopService shopService;

    public CouponsShopRelationServiceImpl(IShopService shopService) {
        this.shopService = shopService;
    }

    @Override
    public List<RelationDTO> getRelatedShopList(Integer id) {
        List<CouponsShopRelation> list = lambdaQuery()
                .eq(CouponsShopRelation::getCouponsId, id)
                .list();

        return list.stream().map(item -> {
            RelationDTO relationDTO = new RelationDTO();
            relationDTO.setId(item.getId());
            relationDTO.setMajorId(item.getCouponsId());
            relationDTO.setMinorId(item.getShopId());
            return relationDTO;
        }).toList();
    }

    @Override
    @Transactional
    public void createRelations(Integer couponsId, List<Integer> plus) {
        // 批量查询所有相关门店是否存在
        List<Integer> existingShopIds = shopService.lambdaQuery()
                .in(Shop::getId, plus)
                .list()
                .stream()
                .map(Shop::getId)
                .toList();

        // 检查是否存在不存在的门店
        List<Integer> nonExistingShopIds = plus.stream()
                .filter(id -> !existingShopIds.contains(id))
                .toList();

        if (!nonExistingShopIds.isEmpty()) {
            throw new CommonException("添加优惠券指定门店失败，请检查relatedShop参数，以下门店ID不存在: " + nonExistingShopIds, INVALID_ARGUMENT);
        }

        // 剔除已经添加了的门店
        Map<Integer, Integer> alreadyExisted = lambdaQuery()
                .eq(CouponsShopRelation::getCouponsId, couponsId)
                .list()
                .stream()
                .collect(Collectors.toMap(
                        CouponsShopRelation::getId,
                        CouponsShopRelation::getShopId
                ));

        // 批量保存优惠券与门店的关系
        List<CouponsShopRelation> relations = existingShopIds.stream()
                .filter(id -> !alreadyExisted.containsValue(id))
                .map(relatedId -> {
                    CouponsShopRelation relation = new CouponsShopRelation();
                    relation.setCouponsId(couponsId);
                    relation.setShopId(relatedId);
                    return relation;
                })
                .collect(Collectors.toList());

        saveBatch(relations);
    }

    @Override
    @Transactional
    public void deleteRelation(Integer couponsId, List<Integer> subtract) {

        // 批量查询要删除的商品是否已经是该优惠券的指定商品
        Map<Integer, Integer> existingToSubtract = lambdaQuery()
                .eq(CouponsShopRelation::getCouponsId, couponsId)
                .in(CouponsShopRelation::getShopId, subtract)
                .list()
                .stream()
                .collect(Collectors.toMap(
                        CouponsShopRelation::getId, // 键为 id
                        CouponsShopRelation::getShopId // 值为 goodsId
                ));

        // 检查是否存在不存在的商品
        List<Integer> nonExistingToSubtract = subtract.stream()
                .filter(id -> !existingToSubtract.containsValue(id))
                .toList();

        if (!nonExistingToSubtract.isEmpty()) {
            throw new CommonException("删除优惠券指定门店失败，该优惠券尚未指定以下门店ID: " + nonExistingToSubtract, INVALID_ARGUMENT);
        }

        removeBatchByIds(existingToSubtract.keySet());
    }
}
