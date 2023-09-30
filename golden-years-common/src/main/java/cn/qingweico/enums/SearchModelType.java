package cn.qingweico.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 主站搜索模式
 *
 * @author zqw
 * @date 2022/4/12
 */
@AllArgsConstructor
@Getter
public enum SearchModelType {
    /**
     * SQL搜索
     */
    SQL(0, "SQL搜索"),
    /**
     * ElasticSearch搜索
     */
    ElasticSearch(1, "ElasticSearch搜索");
    public final Integer val;
    public final String desc;
}
