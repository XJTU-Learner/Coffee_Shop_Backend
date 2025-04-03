package org.xjtu_learner.coffee_shop.common.auth.session;

public interface ISessionManager<P,D> {

    /**
     * 创建会话
     */
    String createSession(P context);

    /**
     * 获取会话
     */
    D getSession(String token);

    /**
     * 获取会话并刷新缓存
     */
    D getSessionAndRefresh(String token);

    /**
     * 删除会话
     */
    void removeSession(String token);

    /**
     * 是否已经登录
     */
    boolean haveSession(String token);
}
