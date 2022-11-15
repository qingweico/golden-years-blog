package cn.qingweico.article.clients;

import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zqw
 * @date 2021/10/19
 */
@Slf4j
@Component
public class ArticleDetailClientFallback implements ArticleDetailClient {

    @Override
    public Result getArticleDetail(String articleId) {
        log.warn("{}, get article detail, 文章服务不可用", DateUtils.dateToStringWithTime());
        return Result.r(Response.SYSTEM_ERROR);
    }
}
