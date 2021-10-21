package cn.qingweico.api.service;

import cn.qingweico.util.PagedGridResult;
import cn.qingweico.util.RedisOperator;
import com.github.pagehelper.PageInfo;
import com.mongodb.client.gridfs.GridFSBucket;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/10
 */
public class BaseService {

    public static final String REDIS_ALL_CATEGORY = "redis_all_category";
    public static final String REDIS_WRITER_FANS_COUNTS = "redis_writer_fans_counts";
    public static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts";
    public static final String REDIS_ARTICLE_COMMENT_COUNTS = "redis_article_comment_counts";
    public static final String REDIS_USER_INFO = "redis_user_info";

    @Autowired
    public RedisOperator redisOperator;

    @Autowired
    public RabbitTemplate rabbitTemplate;


    public PagedGridResult setterPagedGrid(List<?> list,
                                           Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(list);
        gridResult.setPage(page);
        gridResult.setRecords(pageList.getTotal());
        gridResult.setTotal(pageList.getPages());
        return gridResult;
    }
}
