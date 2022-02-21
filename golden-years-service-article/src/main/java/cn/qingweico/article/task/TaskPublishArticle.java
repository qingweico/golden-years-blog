package cn.qingweico.article.task;

import cn.qingweico.article.service.ArticleService;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * {@code @Configuration  @EnableScheduling}
 * 定时发表文章
 *
 * @author zqw
 * @date 2021/9/11
 */
public class TaskPublishArticle {

    @Resource
    private ArticleService articleService;


    @Scheduled(cron = "0/3 * * * * ?")
    public void publishArticles() {
        articleService.timedPublishArticle();
    }

}
