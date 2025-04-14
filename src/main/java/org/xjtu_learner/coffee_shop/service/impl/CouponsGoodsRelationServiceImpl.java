package org.xjtu_learner.coffee_shop.service.impl;

import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.RelationDTO;
import org.xjtu_learner.coffee_shop.entity.po.CouponsGoodsRelation;
import org.xjtu_learner.coffee_shop.dao.CouponsGoodsRelationMapper;
import org.xjtu_learner.coffee_shop.entity.po.Goods;
import org.xjtu_learner.coffee_shop.service.ICouponsGoodsRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xjtu_learner.coffee_shop.service.IGoodsService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.INVALID_ARGUMENT;

/**
 * <p>
 * 优惠卷使用规则对应的指定商品，如优惠卷用于‘美国拿铁’ 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class CouponsGoodsRelationServiceImpl extends ServiceImpl<CouponsGoodsRelationMapper, CouponsGoodsRelation> implements ICouponsGoodsRelationService {

    private final IGoodsService goodsService;

    public CouponsGoodsRelationServiceImpl(IGoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @Override
    public List<RelationDTO> getRelatedGoodsList(Integer id) {
        List<CouponsGoodsRelation> list = lambdaQuery()
                .eq(CouponsGoodsRelation::getCouponsId, id)
                .list();

        return list.stream().map(item -> {
            RelationDTO relationDTO = new RelationDTO();
            relationDTO.setId(item.getId());
            relationDTO.setMajorId(item.getCouponsId());
            relationDTO.setMinorId(item.getGoodsId());
            return relationDTO;
        }).toList();
    }

    @Override
    @Transactional
    public void createRelations(Integer couponsId, List<Integer> plus) {
        // 批量查询所有相关商品是否存在
        List<Integer> existingGoodsIds = goodsService.lambdaQuery()
                .in(Goods::getId, plus)
                .list()
                .stream()
                .map(Goods::getId)
                .toList();

        // 检查是否存在不存在的商品
        List<Integer> nonExistingGoodsIds = plus.stream()
                .filter(id -> !existingGoodsIds.contains(id))
                .toList();

        if (!nonExistingGoodsIds.isEmpty()) {
            throw new CommonException("添加优惠券指定商品失败，以下商品ID不存在: " + nonExistingGoodsIds, INVALID_ARGUMENT);
        }

        // 剔除已经添加了的商品
        Map<Integer, Integer> alreadyExisted = lambdaQuery()
                .eq(CouponsGoodsRelation::getCouponsId, couponsId)
                .list()
                .stream()
                .collect(Collectors.toMap(
                        CouponsGoodsRelation::getId,
                        CouponsGoodsRelation::getGoodsId
                ));

        // 批量保存优惠券与商品的关系
        List<CouponsGoodsRelation> relations = existingGoodsIds.stream()
                .filter(id -> !alreadyExisted.containsValue(id))
                .map(relatedId -> {
                    CouponsGoodsRelation relation = new CouponsGoodsRelation();
                    relation.setCouponsId(couponsId);
                    relation.setGoodsId(relatedId);
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
                .eq(CouponsGoodsRelation::getCouponsId, couponsId)
                .in(CouponsGoodsRelation::getGoodsId, subtract)
                .list()
                .stream()
                .collect(Collectors.toMap(
                        CouponsGoodsRelation::getId, // 键为 id
                        CouponsGoodsRelation::getGoodsId // 值为 goodsId
                ));

        // 检查是否存在不存在的商品
        List<Integer> nonExistingToSubtract = subtract.stream()
                .filter(id -> !existingToSubtract.containsValue(id))
                .toList();

        if (!nonExistingToSubtract.isEmpty()) {
            throw new CommonException("删除优惠券指定商品失败，该优惠券尚未指定以下商品ID: " + nonExistingToSubtract, INVALID_ARGUMENT);
        }

        removeBatchByIds(existingToSubtract.keySet());
    }
}
