package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.entity.dto.CouponsMemberDTO;
import org.xjtu_learner.coffee_shop.entity.po.Coupons;
import org.xjtu_learner.coffee_shop.entity.po.CouponsMemberRelation;
import org.xjtu_learner.coffee_shop.dao.CouponsMemberRelationMapper;
import org.xjtu_learner.coffee_shop.service.ICouponsMemberRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xjtu_learner.coffee_shop.service.ICouponsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.*;

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

    /**
     * 检查某用户优惠券关系是否存在，并返回对应优惠券id
     * @param memberId 用户id
     * @param couponsMemberRelationId 用户优惠券关系id
     * @return 对应优惠券id
     */
    @Override
    @Transactional
    public Integer checkValid(Integer memberId, Integer couponsMemberRelationId) {

        CouponsMemberRelation one = lambdaQuery()
                .eq(CouponsMemberRelation::getId, couponsMemberRelationId)
                .eq(CouponsMemberRelation::getMemberId, memberId)
                .eq(CouponsMemberRelation::getIsUsed, false)
                .eq(CouponsMemberRelation::getIsExpired, false)
                .one();

        if (one == null) {
            throw new CommonException("用户未拥有该优惠券", NOT_EXIST);
        }

        // 检查优惠券是否可用
        if (LocalDateTime.now().isBefore(one.getStartTime())) {
            throw new CommonException("该优惠券尚未到使用时间：" + one.getStartTime(), NOT_AVAILABLE);
        }

        // 检查优惠券是否过期
        if (LocalDateTime.now().isAfter(one.getEndTime())) {
            lambdaUpdate()
                    .set(CouponsMemberRelation::getIsExpired, true)
                    .eq(CouponsMemberRelation::getId, couponsMemberRelationId)
                    .eq(CouponsMemberRelation::getIsExpired, false)
                    .update();
            throw new CommonException("该优惠券已过期", EXPIRED);
        }

        return one.getCouponsId();
    }
}
