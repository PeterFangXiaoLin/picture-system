package com.my.constant;

public interface UserConstant {
    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    /**
     * 默认用户名
     */
    String DEFAULT_USER_NAME = "无名";

    /**
     * 默认密码
     */
    String DEFAULT_PASSWORD = "12345678";

    /**
     * 最小账号长度
     */
    int minUserAccountLen = 4;

    /**
     * 最长账号长度
     */
    int maxUserAccountLen = 16;


    /**
     * 最小密码长度
     */
    int minPasswordLen = 8;

    /**
     * 最长密码长度
     */
    int maxPasswordLen = 20;

    //  region 权限
    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";
    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";
    // endregion
}
