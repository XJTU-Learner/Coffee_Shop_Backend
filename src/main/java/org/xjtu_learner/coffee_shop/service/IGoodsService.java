package org.xjtu_learner.coffee_shop.service;

import org.xjtu_learner.coffee_shop.entity.form.GoodsForm;
import org.xjtu_learner.coffee_shop.entity.dto.PageDTO;
import org.xjtu_learner.coffee_shop.entity.form.PageQuery;
import org.xjtu_learner.coffee_shop.entity.po.Goods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
public interface IGoodsService extends IService<Goods> {

    List<Goods> getGoodsList(List<Integer> idList);

    PageDTO<Goods> getGoodsPage(PageQuery pageQuery);

    void createGoods(GoodsForm form);

    void updateGoods(GoodsForm form);

    void deleteGoods(Integer id);

    PageDTO<Goods> getNewGoodsList(PageQuery pageQuery);

    boolean checkGoodsIdValid(Integer goodsId);

    boolean checkGoodsIdValidBatch(List<Integer> goodsIdList);
}
