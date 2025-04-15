package org.xjtu_learner.coffee_shop.entity.dto;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.HashMap;

@Data
public class PageQuery {
    public static final Long DEFAULT_PAGE_SIZE = 20L;
    public static final Long DEFAULT_PAGE_NUM = 1L;
    public static final String DEFAULT_SORT_BY = "create_at";

    /**
     * 页码
     */
    @Min(value = 1, message = "页码不能小于1")
    private Long pageNo = DEFAULT_PAGE_NUM;

    /**
     * 页大小
     */
    @Min(value = 1, message = "每页查询数量不能小于1")
    private Long pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 是否升序
     */
    private Boolean isAsc = true;

    /**
     * 排序字段
     */
    private String sortBy = DEFAULT_SORT_BY;


    public <T> Page<T> toMpPage(OrderItem... orderItems) {
        Page<T> page = new Page<>(pageNo, pageSize);
        // 是否手动指定排序方式
        if (orderItems != null && orderItems.length > 0) {
            for (OrderItem orderItem : orderItems) {
                page.addOrder(orderItem);
            }
            return page;
        }
        // 前端是否有排序字段
        if (StrUtil.isNotBlank(sortBy)){
            OrderItem orderItem = new OrderItem();
            orderItem.setAsc(isAsc);
            orderItem.setColumn(sortBy);
            page.addOrder(orderItem);
        }
        return page;
    }

    public <T> Page<T> toMpPage(String defaultSortBy, boolean isAsc) {
        if (StrUtil.isBlank(sortBy)){
            sortBy = defaultSortBy;
            this.isAsc = isAsc;
        }
        Page<T> page = new Page<>(pageNo, pageSize);
        OrderItem orderItem = new OrderItem();
        orderItem.setAsc(this.isAsc);
        orderItem.setColumn(sortBy);
        page.addOrder(orderItem);
        return page;
    }
    public <T> Page<T> toMpPageByCreateTimeDesc() {
        return toMpPage(DEFAULT_SORT_BY, false);
    }

}
