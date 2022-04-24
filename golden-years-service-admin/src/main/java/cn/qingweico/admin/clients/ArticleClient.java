package cn.qingweico.admin.clients;

import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.result.GraceJsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * @author zqw
 * @date 2022/4/18
 */
@FeignClient(value = "service-article", path = "/portal/article")
public interface ArticleClient {
    /**
     * 根据文章状态获取所有的文章数量
     * <p>
     * {@link ArticleReviewStatus}
     * {@link YesOrNo} 1: 非物理删除; 0: 未删除
     *
     * @return {@code Integer}文章数量
     */
    @GetMapping("/getArticleCounts")
    Integer getArticleCounts();


    /**
     * 获取全站评论数量
     *
     * @return {@code Integer}评论数量
     */
    @GetMapping("/getCommentCount")
    Integer getCommentCount();

    /**
     * 获取每个标签下文章数目
     *
     * @return 每个标签下文章数目 [{tagName: xxx, articleCount: xxx}]
     */
    @GetMapping("/getBlogCountByTag")
    GraceJsonResult getBlogCountByTag();

    /**
     * 首页查询每个类别的文章数量
     *
     * @return GraceJsonResult
     */
    @GetMapping("/category/getCategoryListWithArticleCount")
    GraceJsonResult getCategoryListWithArticleCount();

    /**
     * 获取一年内的文章贡献数
     *
     * @return GraceJsonResult
     */
    @GetMapping("/getBlogContributeCount")
    GraceJsonResult getBlogContributeCount();
}
