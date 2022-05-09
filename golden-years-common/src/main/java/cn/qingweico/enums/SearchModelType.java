package cn.qingweico.enums;

/**
 * 主站搜索模式
 *
 * @author zqw
 * @date 2022/4/12
 */
public enum SearchModelType {
    /**
     * SQL搜索
     */
    SQL(0, "SQL搜索"),
    /**
     * ElasticSearch搜索
     */
    ElasticSearch(1, "ElasticSearch搜索");
    public final Integer type;
    public final String value;

    SearchModelType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
