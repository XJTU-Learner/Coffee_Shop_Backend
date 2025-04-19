package org.xjtu_learner.coffee_shop.common.constant;

import cn.hutool.core.collection.CollectionUtil;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Period;
import java.util.List;

public class RuleConstant {

    public static final BigDecimal POINTS_ACQUISITION_RATIO = BigDecimal.valueOf(0.02); // 1/50

//    public static final Duration PAYMENT_DEADLINE = Duration.ofMinutes(10); // 10 min
//
//    public static final List<Long> DEFAULT_DELAY_INTERVAL =
//            CollectionUtil.newArrayList(10000L, 10000L, 10000L, 30000L, 60000L, 180000L, 300000L);
//                                        //      10s     10s     10s     30s     60s     180s     300s

    public static final Duration PAYMENT_DEADLINE = Duration.ofMinutes(10); // 5 min

    public static final List<Long> DEFAULT_DELAY_INTERVAL =
            CollectionUtil.newArrayList(10000L, 10000L, 10000L, 30000L, 60000L, 180000L);
                                        //      10s     10s     10s     30s     60s     180s
}
