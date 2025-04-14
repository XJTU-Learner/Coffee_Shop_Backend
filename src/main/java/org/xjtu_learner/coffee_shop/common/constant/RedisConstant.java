package org.xjtu_learner.coffee_shop.common.constant;

public class RedisConstant {

    public static final String SESSION_CODE_KEY = "SESSION:CODE:";
    public static final String ADMIN_SESSION_CODE_PREFIX = "ADMIN:";
    public static final String MEMBER_SESSION_CODE_PREFIX = "MEMBER:";
    public static final String MERCHANT_SESSION_CODE_PREFIX = "MERCHANT:";
    public static final Long SESSION_CODE_TTL = 5L;

    public static final String ADMIN_SESSION_PREFIX = "SESSION:TOKEN:ADMIN:";
    public static final Long ADMIN_SESSION_TTL = 3000L;   // min

    public static final String MEMBER_SESSION_PREFIX = "SESSION:TOKEN:MEMBER:";
    public static final Long MEMBER_SESSION_TTL = 3000L;

    public static final String MERCHANT_SESSION_PREFIX = "SESSION:TOKEN:MERCHANT:";
    public static final Long MERCHANT_SESSION_TTL = 3000L;


    public static final String LOCK_PREFIX = "LOCK:";
    public static final Long LOCK_TTL = 10L;    // sec

    public static final String CACHE_KEY = "CACHE:";
    public static final Long DEFAULT_CACHE_ID_TTL = 10L;   // min
    public static final Long DEFAULT_CACHE_LIST_TTL = 30L;   // min
    public static final Long CACHE_BLANK_TTL = 2L;  // min
    public static final String CACHE_GOODS_LIST_KEY = CACHE_KEY + "GOODSLIST";


}
