package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 文章审核状态
 * 文章状态:
 * 1 ---> 审核中(用户已提交)
 * 2 ---> 审核通过
 * 3 ---> 审核不通过
 * 4 ---> 文章撤回
 *
 * @author zqw
 * @date 2021/9/11
 */
@AllArgsConstructor
public enum ArticleReviewStatus {
    /**
     * 审核中(用户已提交)
     */
    REVIEWING(1, "审核中(用户已提交)"),

    /**
     * 审核通过(已发布)
     */
    SUCCESS(2, "审核通过(已发布)"),
    /**
     * 审核未通过
     */
    FAILED(3, "审核未通过"),
    /**
     * 文章撤回
     */
    WITHDRAW(4, "文章撤回");


    public final Integer type;
    public final String value;

    /**
     * 判断传入的审核状态是不是有效的值
     *
     * @param value 文章状态
     * @return boolean -> 文章状态是否有效
     */
    public static boolean isArticleStatusValid(Integer value) {
        if (value != null) {
            return value.equals(REVIEWING.type)
                    || value.equals(SUCCESS.type)
                    || value.equals(FAILED.type)
                    || value.equals(WITHDRAW.type);
        }
        return false;
    }
}
