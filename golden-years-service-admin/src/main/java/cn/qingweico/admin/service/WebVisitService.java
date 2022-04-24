package cn.qingweico.admin.service;

import java.util.Map;

/**
 * @author zqw
 * @date 2022/4/18
 */
public interface WebVisitService {
    /**
     * 获取今日网站访问人数
     *
     * @return int
     */
    int getWebVisitCount();

    /**
     * 获取近七天的访问量
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getVisitByWeek();
}
