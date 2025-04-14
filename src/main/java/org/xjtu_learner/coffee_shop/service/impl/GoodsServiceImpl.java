package org.xjtu_learner.coffee_shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import org.xjtu_learner.coffee_shop.common.exception.CommonException;
import org.xjtu_learner.coffee_shop.common.utils.CacheAgent;
import org.xjtu_learner.coffee_shop.entity.dto.GoodsForm;
import org.xjtu_learner.coffee_shop.entity.dto.PageDTO;
import org.xjtu_learner.coffee_shop.entity.dto.PageQuery;
import org.xjtu_learner.coffee_shop.entity.po.Goods;
import org.xjtu_learner.coffee_shop.dao.GoodsMapper;
import org.xjtu_learner.coffee_shop.service.IGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.INVALID_ARGUMENT;
import static org.xjtu_learner.coffee_shop.common.constant.ExceptionCodeConstant.UPDATE_FAILED;
import static org.xjtu_learner.coffee_shop.common.constant.RedisConstant.CACHE_GOODS_LIST_KEY;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author xuezhihengg
 * @since 2025-04-03
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Override
    public PageDTO<Goods> getGoodsList(PageQuery pageQuery) {

        Page<Goods> page = lambdaQuery()
                .page(pageQuery.toMpPage(pageQuery.getSortBy(), pageQuery.getIsAsc()));

        return PageDTO.of(page, Goods.class);
    }

    @Override
    @Transactional
    public void createGoods(GoodsForm form) {
        if (form.getId() != null) {
            throw new CommonException("不需要指定主键id", INVALID_ARGUMENT);
        }
        Goods newGoods = BeanUtil.copyProperties(form, Goods.class, "id");
        save(newGoods);
    }

    @Override
    @Transactional
    public void updateGoods(GoodsForm form) {
        if(form.getId() == null){
            throw new CommonException("未指定指定主键id", INVALID_ARGUMENT);
        }
        Goods goods = BeanUtil.copyProperties(form, Goods.class);
        goods.setUpdateAt(LocalDateTime.now());
        boolean success = lambdaUpdate()
                .eq(Goods::getId, form.getId())
                .update(goods);

        if(!success){
            throw new CommonException("更新失败，可能原因：该商品不存在",UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteGoods(Integer id) {
        if(id == null){
            throw new CommonException("未指定指定主键id", INVALID_ARGUMENT);
        }
        boolean success = lambdaUpdate()
                .eq(Goods::getId, id)
                .eq(Goods::getForSale, true)
                .set(Goods::getUpdateAt,LocalDateTime.now())
                .set(Goods::getForSale, false)
                .update();

        if(!success){
            throw new CommonException("下架失败，可能原因：该商品已经下架或商品不存在",UPDATE_FAILED);
        }
    }
}
