package com.mojian.common;

public class Constants {
    public static final String UTF8 = "UTF-8";

    public static final String DEFAULT_PASSWORD = "123456";

    public static final String ADMIN = "admin";

    public static final String TEST = "test";

    public static final String UNKNOWN = "未知";

    public static final int YES = 1;

    public static final int NO = 0;

    /** 用户类型：前台用户 */
    public static final int USER_TYPE_FRONT = 0;

    /** 用户类型：后台用户 */
    public static final int USER_TYPE_ADMIN = 1;

    public static final String CURRENT_USER = "current_user";

    public static final String PARENT_VIEW = "ParentView";

    public static final Object USER = "user";

    public static final Long XIAO_ASSISTANT_ID  = 2L;

    // ===== 第三方登录重定向路径（域名从 sys_config 表的 admin_base_url / front_base_url 动态获取） =====

    /** 前台首页路径 */
    public static final String FRONT_HOME_PATH = "/";
    /** 前台登录成功路径（拼接token） */
    public static final String FRONT_LOGIN_SUCCESS_PATH = "/?token=";

    /** 后台登录页路径 */
    public static final String ADMIN_LOGIN_PATH = "/admin/login";
    /** 后台登录成功路径（拼接token） */
    public static final String ADMIN_LOGIN_SUCCESS_PATH = "/admin/?token=";
    /** 后台绑定成功路径 */
    public static final String ADMIN_BIND_SUCCESS_PATH = "/admin/bindCallback?result=success";
    /** 后台绑定失败路径 */
    public static final String ADMIN_BIND_FAIL_PATH = "/admin/bindCallback?result=fail&msg=";

    /** 前台绑定成功路径 */
    public static final String FRONT_BIND_SUCCESS_PATH = "/bindCallback?result=success";
    /** 前台绑定失败路径 */
    public static final String FRONT_BIND_FAIL_PATH = "/bindCallback?result=fail&msg=";

    /** 第三方登录配置缓存名 */
    public static final String CACHE_THIRD_PARTY_CONFIG = "third_party_config";
    /** 第三方登录配置缓存键 - 前台已启用列表 */
    public static final String CACHE_THIRD_PARTY_FRONT_ENABLED = "front_enabled_list";
    /** 第三方登录配置缓存键 - 后台已启用列表 */
    public static final String CACHE_THIRD_PARTY_ADMIN_ENABLED = "admin_enabled_list";

    /** 前台菜单缓存名 */
    public static final String CACHE_FRONT_MENU = "front_menu";
    /** 前台菜单导航列表缓存键 */
    public static final String CACHE_FRONT_MENU_NAV = "nav_list";

    /** 系统参数配置缓存名 */
    public static final String CACHE_SYS_CONFIG = "sys_config";
}
