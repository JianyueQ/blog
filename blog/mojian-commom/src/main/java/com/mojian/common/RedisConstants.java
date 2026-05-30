package com.mojian.common;

/**
 * Redis常量
 */
public class RedisConstants {

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN = "login:token:";

    /**
     * 用户签到
     */
    public static final String USER_SIGN = "user_sign:";


    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_code:";

    /**
     * 滑块验证码 redis key
     */
    public static final String SLIDER_CAPTCHA_CODE_KEY = "slider_captcha_code:";

    /**
     * 访客
     */
    public static final String UNIQUE_VISITOR = "unique_visitor";

    /**
     * 访客量
     */
    public static final String UNIQUE_VISITOR_COUNT = "unique_visitor_count";

    /**
     * 博客浏览量
     */
    public static final String BLOG_VIEWS_COUNT = "blog_views_count";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";


    /**
     * 过期时间 1分钟
     */
    public static final long MINUTE_EXPIRE = 60;

    /**
     * 过期时间 5分钟
     */
    public static final long FIVE_MINUTES_EXPIRE = 5 * 60;

    /**
     * 过期时间 30分钟
     */
    public static final long HALF_HOUR_EXPIRE = 30 * 60;

    /**
     * 过期时间 1小时
     */
    public static final long HOUR_EXPIRE = 60 * 60;

    /**
     * 过期时间 1天
     */
    public static final long DAY_EXPIRE = 24 * 60 * 60;

    /**
     * 过期时间 7天
     */
    public static final long WEEK_EXPIRE = 7 * 24 * 60 * 60;

    /**
     * 过期时间 15天
     */
    public static final long FIFTEEN_DAYS_EXPIRE = 15 * 24 * 60 * 60;

    /**
     * 过期时间 30天
     */
    public static final long MONTH_EXPIRE = 30 * 24 * 60 * 60;


    /**
     * 微信登录用户状态
     */
    public static final String WX_LOGIN_USER_CODE = "wx_login_user_statue:";

    /**
     * 微信登录用户信息
     */
    public static final String WX_LOGIN_USER = "wx_login_user:";

    /**
     * 文章阅读量
     */
    public static final String ARTICLE_QUANTITY = "article_quantity";

    /**
     * 网站配置
     */
    public static final String WEB_CONFIG_KEY = "sys_webconfig";

    /**
     * Redis缓存Key：验证码图片URL Hash
     */
    public static final String CODE_IMAGE_URLS_KEY = "code:image:urls";

    /**
     * Redis缓存Key：验证码图片权重 ZSet
     */
    public static final String CODE_IMAGE_SCORES_KEY = "code:image:scores";

    /**
     * 邮件配置缓存 Key
     */
    public static final String EMAIL_CONFIG_KEY = "email:config";

    /**
     * 邮件发送次数 Key（后缀为邮箱地址）
     */
    public static final String EMAIL_SEND_COUNT_KEY = "email:send_count:";

    /**
     * 每日邮件发送次数上限
     */
    public static final int EMAIL_DAILY_LIMIT = 5;
}
