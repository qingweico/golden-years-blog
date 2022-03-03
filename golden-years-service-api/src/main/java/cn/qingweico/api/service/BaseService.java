package cn.qingweico.api.service;

import cn.qingweico.util.PagedGridResult;
import cn.qingweico.util.RedisOperator;
import com.github.pagehelper.PageInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
public class BaseService {
    @Autowired
    public RedisOperator redisOperator;

    @Autowired
    public RabbitTemplate rabbitTemplate;


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
}
