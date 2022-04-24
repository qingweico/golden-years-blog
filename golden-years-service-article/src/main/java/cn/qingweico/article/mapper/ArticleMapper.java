package cn.qingweico.article.mapper;

import cn.qingweico.mapper.MyMapper;
import cn.qingweico.pojo.Article;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
     *
     * @param yearAndMonth 年份和月份; 格式为 1999-08
     * @return List<Article>
     */
    List<Article> getArticleByTime(String yearAndMonth);


    /**
     * 获取归档时间列表
     *
     * @param userId 用户id
     * @return List<String>
     */
    List<String> queryArchiveTimeList(String userId);

    /**
     * 通过标签获取博客数量
     *
     * @return [{tagName: articleCount}]
     */
    @Select("SELECT tags, COUNT(tags) as count FROM  t_article where article_status = 2 GROUP BY tags")
    List<Map<String, Object>> getBlogCountByTag();

    /**
     * 获取一年内的文章贡献数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return List<Map < String, Object>>
     */
    @Select("SELECT DISTINCT DATE_FORMAT(create_time, '%Y-%m-%d') DATE, COUNT(id) COUNT FROM t_article " +
            "WHERE 1=1 && article_status = 2 && create_time >= #{startTime} && create_time < #{endTime} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')")
    List<Map<String, Object>> getBlogContributeCount(@Param("startTime") String startTime, @Param("endTime") String endTime);
}