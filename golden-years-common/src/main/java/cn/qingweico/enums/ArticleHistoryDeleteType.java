package cn.qingweico.enums;

/**
 * @author zqw
 * @date 2022/5/6
 */
public enum ArticleHistoryDeleteType {
    /**
     * 过去的一小时
     */
    PAST_AN_HOUR(0, "过去的一小时"),
    /**
     * 过去的一天
     */
    PAST_AN_DAY(1, "过去的一天"),
    /**
     * 过去的三天
     */
    PAST_THREE_DAY(2, "过去的三天"),
    /**
     * 过去的一周
     */
    PAST_AN_WEEK(3, "过去的一周");


    public final Integer type;
    public final String value;

    ArticleHistoryDeleteType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
