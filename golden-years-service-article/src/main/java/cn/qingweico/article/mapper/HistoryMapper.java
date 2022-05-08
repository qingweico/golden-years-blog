package cn.qingweico.article.mapper;

import cn.qingweico.mapper.MyMapper;
import cn.qingweico.pojo.History;
import org.springframework.stereotype.Repository;

/**
 * @author zqw
 * @date 2022/5/6
 */
@Repository
public interface HistoryMapper extends MyMapper<History> {
    /**
     * 根据时间删除用户的浏览历史
     * @param time {@code 删除time之前的所有浏览历史}
     */
    void deleteHistoryByTime(String time);
}
