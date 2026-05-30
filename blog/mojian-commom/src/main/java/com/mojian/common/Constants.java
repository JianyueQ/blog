package com.mojian.common;

public class Constants {
    public static final String UTF8 = "UTF-8";

    public static final String DEFAULT_PASSWORD = "123456";

    public static final String ADMIN = "admin";

    public static final String TEST = "test";

    public static final String UNKNOWN = "未知";

    public static final int YES = 1;

    public static final int NO = 0;

    public static final String CURRENT_USER = "current_user";

    public static final String PARENT_VIEW = "ParentView";

    public static final Object USER = "user";

    public static final Long XIAO_ASSISTANT_ID  = 2L;

    public static final String LOGIN_URL = "https://gp.jianyue.cloud/";
    public static final String LOGIN_SUCCESS_URL = "https://gp.jianyue.cloud/?token=";

    /** 后台登录页地址 */
    public static final String ADMIN_LOGIN_URL = "https://gp.jianyue.cloud/admin/login";
    /** 后台登录成功重定向地址 */
    public static final String ADMIN_LOGIN_SUCCESS_URL = "https://gp.jianyue.cloud/admin/?token=";
    /** 后台绑定成功重定向地址 */
    public static final String ADMIN_BIND_SUCCESS_URL = "https://gp.jianyue.cloud/admin/bindCallback?result=success";
    /** 后台绑定失败重定向地址 */
    public static final String ADMIN_BIND_FAIL_URL = "https://gp.jianyue.cloud/admin/bindCallback?result=fail&msg=";

    /** 第三方登录配置缓存名 */
    public static final String CACHE_THIRD_PARTY_CONFIG = "third_party_config";
    /** 第三方登录配置缓存键 - 前台已启用列表 */
    public static final String CACHE_THIRD_PARTY_FRONT_ENABLED = "front_enabled_list";
    /** 第三方登录配置缓存键 - 后台已启用列表 */
    public static final String CACHE_THIRD_PARTY_ADMIN_ENABLED = "admin_enabled_list";
}
