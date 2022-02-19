package cn.qingweico.article.task;

import cn.qingweico.article.service.ArticleService;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * 定时发表文章
 *
 * @author:qiming
 * @date: 2021/9/11
 */
//@Configuration
//@EnableScheduling
public class TaskPublishArticle {

    @Resource
    private ArticleService articleService;


    @Scheduled(cron = "0/3 * * * * ?")
    public void publishArticles() {
        articleService.timedPublishArticle();
    }

}
