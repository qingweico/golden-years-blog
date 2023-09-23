package cn.qingweico.global;

/**
 * Redis 常量
 * @author zqw
 * @date 2022/2/23
 */
public class RedisConst {
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_INFO = "redis_user_info";
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";
    public static final String REDIS_ADMIN_INFO = "redis_admin_info";
    public static final String REDIS_ARTICLE_ALREADY_READ = "redis_article_already_read";
    public static final String MOBILE_SMS_CODE = "mobile_sms_code";
    public static final String REDIS_IP = "redis_ip";
    public static final String REDIS_ARTICLE_CATEGORY = "redis_article_category";
    public static final String REDIS_ARTICLE_CATEGORY_WITH_ARTICLE_COUNT = "redis_article_category_with_article_count";
    public static final String REDIS_ARTICLE_TAG = "redis_article_tag";
    public static final String REDIS_AUTHOR_FANS_COUNTS = "redis_author_fans_counts";
    public static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts";
    public static final String REDIS_LIMIT_VISIT_IP = "redis_visit_ip";
    public static final String REDIS_LIMIT_FORBID_IP = "redis_forbid_ip";
    public static final String REDIS_WEB_CONFIG = "redis_web_config";
    public static final String REDIS_SYSTEM_CONFIG = "redis_system_config";
    public static final String REDIS_FRIEND_LINK = "redis_friend_link";
    public final static String ALL = "ALL";
    public static final String REDIS_ARTICLE_READ_COUNTS = "redis_article_read_counts";
    public static final String REDIS_ARTICLE_DETAIL = "redis_article_detail";
    public static final String REDIS_ARTICLE_COMMENT_COUNTS = "redis_article_comment_counts";
    public static final String REDIS_ARTICLE_COLLECT_COUNTS = "redis_article_collect_counts";
    public static final String REDIS_ARTICLE_STAR_COUNTS = "redis_article_star_counts";
    public static final String REDIS_ARTICLE_ALREADY_STAR = "redis_article_already_star";
    public static final String REDIS_ARTICLE_ALREADY_COLLECT = "redis_article_already_collect";
    public final static String DASHBOARD = "dashboard";
    public final static String WEEK_VISIT = "week_visit";
    public final static String BLOG_COUNT_BY_TAG = "blog_count_by_tag";
    public final static String BLOG_CONTRIBUTE_COUNT = "blog_contribute_count";
    public final static String Z_SET_ARTICLE_RANK = "z_set_article_rank";
    public final static String Z_SET_ARTICLE_USER_RANK = "z_set_article_user_rank";
    public final static long CACHE_NULL_TTL = 2;

    public final static long LOCK_TTL = 2;
    public final static String LOCK = "LOCK";
    public final static String LOCK_VALUE = "LOCK_VALUE";
    public final static long LOCK_TIME = 10;


    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 登录限制
     */
    public final static String LOGIN_LIMIT = "login_limit:";
}
