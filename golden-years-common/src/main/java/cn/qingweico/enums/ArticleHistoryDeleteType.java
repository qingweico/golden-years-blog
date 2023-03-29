package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 文章浏览历史的删除模式
 * @author zqw
 * @date 2022/5/6
 */
@AllArgsConstructor
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
}
