package cn.qingweico.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文章审核结果
 *
 * @author zqw
 * @date 2021/9/11
 */
@AllArgsConstructor
@Getter
public enum ArticleReviewResult {

    /**
     * 审核通过
     */
    PASS("pass", "审核通过"),

    /**
     * 审核不通过
     */
    FAIL("fail", "审核不通过");
    private final String val;
    private final String desc;
}
