package cn.qingweico.enums;

/**
 * 文章封面类型
 *
 * @author zqw
 * @date 2021/9/11
 */
public enum ArticleCoverType {
    /**
     * 文章封面 - 单图
     */
    ONE_IMAGE(1, "单图"),
    /**
     * 文章封面 - 纯文字
     */
    WORDS(2, "纯文字");

    public final Integer type;
    public final String value;

    ArticleCoverType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
