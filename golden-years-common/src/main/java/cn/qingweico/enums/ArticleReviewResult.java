package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 文章审核结果
 *
 * @author zqw
 * @date 2021/9/11
 */
@AllArgsConstructor
public enum ArticleReviewResult {

    /**
     * 审核通过
     */
    PASS("pass", "审核通过"),

    /**
     * 审核不通过
     */
    FAIL("fail", "审核不通过");

    public final String type;
    public final String value;
}
