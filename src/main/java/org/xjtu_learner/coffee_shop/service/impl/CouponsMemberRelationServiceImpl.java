package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import org.xjtu_learner.coffee_shop.entity.dto.CouponsMemberDTO;
import org.xjtu_learner.coffee_shop.entity.dto.GoodsDTO;
import org.xjtu_learner.coffee_shop.entity.dto.ShopGoodsDTO;
import org.xjtu_learner.coffee_shop.entity.po.Coupons;
import org.xjtu_learner.coffee_shop.entity.po.CouponsMemberRelation;
import org.xjtu_learner.coffee_shop.dao.CouponsMemberRelationMapper;
import org.xjtu_learner.coffee_shop.entity.po.Goods;
import org.xjtu_learner.coffee_shop.entity.po.ShopGoodsRelation;
import org.xjtu_learner.coffee_shop.service.ICouponsMemberRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xjtu_learner.coffee_shop.service.ICouponsService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠卷用户关联表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class CouponsMemberRelationServiceImpl extends ServiceImpl<CouponsMemberRelationMapper, CouponsMemberRelation> implements ICouponsMemberRelationService {

    private final ICouponsService couponsService;

    public CouponsMemberRelationServiceImpl(ICouponsService couponsService) {
        this.couponsService = couponsService;
    }

    @Override
    public List<CouponsMemberRelation> getCouponsMemberRelationList(Integer memberId) {

        return lambdaQuery()
                .eq(CouponsMemberRelation::getMemberId, memberId)
                .eq(CouponsMemberRelation::getIsValid, true)
                .eq(CouponsMemberRelation::getIsUsed, false)
                .list();
    }

    @Override
    public List<CouponsMemberDTO> getCouponsMemberDTOList(List<CouponsMemberRelation> relationList) {
        List<CouponsMemberDTO> all = relationList.stream()
                .map((po) -> (BeanUtil.copyProperties(po, CouponsMemberDTO.class)))
                .toList();



        // 查询Coupons缓存填充CouponsMemberDTO中
        List<Integer> couponsIdList = relationList.stream()
                .map(CouponsMemberRelation::getCouponsId)
                .toList();

        List<Coupons> couponsList = couponsService.getCouponsList(couponsIdList);
        Map<Integer, Coupons> couponsMap = couponsList.stream()
                .collect(Collectors.toMap(
                        Coupons::getId,
                        (po) -> (po)
                ));

        // 遍历商品ID列表，组装对象列表
        all.forEach(
                dto -> BeanUtil.copyProperties(couponsMap.get(dto.getCouponsId()), dto)
        );

        return all;
    }
}
