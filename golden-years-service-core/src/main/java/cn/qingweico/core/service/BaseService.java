package cn.qingweico.core.service;

import cn.qingweico.entity.Article;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.redis.RedisCache;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
public class BaseService {
    @Resource(name = "rt")
    public RedisCache redisCache;
    @Autowired
    public RabbitTemplate rabbitTemplate;
    @Autowired
    public ElasticsearchTemplate esTemplate;

    public PagedResult setterPagedGrid(List<?> list,
                                       Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedResult gridResult = new PagedResult();
        gridResult.setRows(list);
        gridResult.setCurrentPage(page);
        gridResult.setRecords(pageList.getTotal());
        gridResult.setTotalPage(pageList.getPages());
        return gridResult;
    }

    public List<Article> filterArticleTag(List<Article> list, String tag) {
        List<Article> result = new ArrayList<>();
        for (Article article : list) {
            String tags = article.getTags();
            String[] tagIds = tags.replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",");
            for (String tagId : tagIds) {
                if (tag.equals(tagId)) {
                    result.add(article);
                    break;
                }
            }
        }
        return result;
    }
}
