package cn.qingweico.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

/**
 * 文章浏览历史的删除模式
 * @author zqw
 * @date 2022/5/6
 */
@AllArgsConstructor
@Getter
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



    private final Integer val;
    private final String desc;
}
