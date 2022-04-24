package cn.qingweico.article.clients;

import cn.qingweico.result.GraceJsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zqw
 * @date 2021/10/19
 */
@FeignClient(value = "service-article", path = "/portal/article", fallback = ArticleDetailClientFallback.class)
public interface ArticleDetailClient {
    /**
     * 获取文章详细信息
     * @param articleId 文章id
     * @return GraceJsonResult
     */
    @GetMapping("detail")
    GraceJsonResult getArticleDetail(@RequestParam("articleId") String articleId);
}