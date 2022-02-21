package cn.qingweico.article.mapper;

import cn.qingweico.my.mapper.MyMapper;
import cn.qingweico.pojo.Article;
import org.springframework.stereotype.Repository;

/**
 * @author zqw
 * @date 2021/9/11
 */
@Repository
public interface ArticleMapper extends MyMapper<Article> {

    /**
     * 定时发布文章(采用定时任务)
     */
    void timedPublishArticle();
}