package cn.qingweico.enums;

/**
 * 文章自动审核结果
 *
 * @author:qiming
 * @date: 2021/9/11
 */
public enum ArticleReviewLevel {

    PASS("pass", "自动审核通过"),

    BLOCK("block", "自动审核不通过"),

    REVIEW("review", "建议人工复审");

    public final String type;
    public final String value;

    ArticleReviewLevel(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
