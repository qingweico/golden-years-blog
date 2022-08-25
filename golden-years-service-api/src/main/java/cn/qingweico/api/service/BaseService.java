package cn.qingweico.api.service;

import cn.qingweico.pojo.Article;
import cn.qingweico.util.PagedGridResult;
import cn.qingweico.util.RedisTemplate;
import com.github.pagehelper.PageInfo;
import org.n3r.idworker.Sid;
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
    public RedisTemplate redisTemplate;
    @Autowired
    public RabbitTemplate rabbitTemplate;
    @Resource
    public ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    public Sid sid;

    public PagedGridResult setterPagedGrid(List<?> list,
                                           Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult gridResult = new PagedGridResult();
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

    public void refreshCache(String key) {
        redisTemplate.del(key);
    }
}
