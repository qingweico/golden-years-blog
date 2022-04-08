package cn.qingweico.article.mapper;

import cn.qingweico.mapper.MyMapper;
import cn.qingweico.pojo.Article;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * 根据年份和月份查询文章
     * @param yearAndMonth 年份和月份; 格式为 1999-08
     * @param pageNumber 起始分页
     * @param pageSize 每页数量
     * @return List<Article>
     */
    List<Article> getArticleByTime(String yearAndMonth, Integer pageNumber, Integer pageSize);


    /**
     * 获取归档时间列表
     * @param userId 用户id
     * @return  List<String>
     */
    List<String> queryArchiveTimeList(String userId);
}