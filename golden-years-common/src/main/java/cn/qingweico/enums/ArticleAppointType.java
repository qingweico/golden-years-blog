package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 文章发布类型
 *
 * @author zqw
 * @date 2021/9/11
 */
@AllArgsConstructor
public enum ArticleAppointType {

    /**
     * 文章定时发布
     */
    TIMING(1, "文章定时发布 - 定时"),

    /**
     * 文章立即发布
     */
    IMMEDIATELY(0, "文章立即发布 - 即时");

    public final Integer type;
    public final String value;
}
