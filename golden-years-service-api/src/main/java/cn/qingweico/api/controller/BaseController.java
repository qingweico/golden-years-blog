package cn.qingweico.api.controller;

import cn.qingweico.util.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author:qiming
 * @date: 2021/9/5
 */
public class BaseController {

    @Autowired
    public RedisOperator redisOperator;

    @Resource
    public RestTemplate restTemplate;

    @Resource
    public ElasticsearchTemplate elasticsearchTemplate;

    public static final String MOBILE_CODE = "mobile_code";
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_ADMIN_TOKEN = "redis_admin_token";
    public static final String REDIS_USER_INFO = "redis_user_info";
    public static final String REDIS_ALL_CATEGORY = "redis_all_category";
    public static final String REDIS_WRITER_FANS_COUNTS = "redis_writer_fans_counts";
    public static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts";
    public static final String REDIS_ARTICLE_READ_COUNTS = "redis_article_read_counts";
    public static final String REDIS_ARTICLE_ALREADY_READ = "redis_article_already_read";
    public static final String REDIS_ARTICLE_COMMENT_COUNTS = "redis_article_comment_counts";
    public static final Integer COOKIE_EXPIRE = 30 * 24 * 60 * 60;
    public static final Integer COOKIE_DELETE = 0;
    public static final Integer COMMON_START_PAGE = 1;
    public static final Integer COMMON_PAGE_SIZE = 5;


    @Value("${website.domain-name}")
    private String DOMAIN_NAME;


    public void setCookie(HttpServletRequest req,
                          HttpServletResponse resp,
                          String cookieName,
                          String cookieValue,
                          Integer maxAge) {
        try {
            cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            setCookieValue(req, resp, cookieName, cookieValue, maxAge);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setCookieValue(HttpServletRequest req,
                               HttpServletResponse resp,
                               String cookieName,
                               String cookieValue,
                               Integer maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(maxAge);
        cookie.setDomain(DOMAIN_NAME);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    public Integer getCountsFromRedis(String key) {
        String counts = redisOperator.get(key);
        if (StringUtils.isBlank(counts)) {
            counts = "0";
        }
        return Integer.valueOf(counts);
    }
}
