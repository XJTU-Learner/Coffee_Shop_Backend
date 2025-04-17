package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.form.CouponsForm;
import org.xjtu_learner.coffee_shop.entity.dto.PageDTO;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.po.Coupons;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 优惠卷表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface ICouponsService extends IService<Coupons> {

    List<Coupons> getCouponsList(List<Integer> couponsIdList);

    PageDTO<Coupons> getCouponsPage(PageQuery pageQuery);

    void createCoupons(CouponsForm form);

    void updateCoupons(CouponsForm form);

    void deleteCoupons(Integer id);

    boolean checkGoodsIdValidBatch(List<Integer> couponsIdList);
}
