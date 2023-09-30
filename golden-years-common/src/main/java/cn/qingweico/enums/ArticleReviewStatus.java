package cn.qingweico.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zqw
 * @date 2021/9/11
 */
@AllArgsConstructor
@Getter
public enum ArticleReviewStatus {
    /**
     * 审核中(用户已提交)
     */
    REVIEWING(1, "审核中(用户已提交)"),

    /**
     * 审核通过(已发布)
     */
    APPROVED(2, "审核通过(已发布)"),
    /**
     * 审核未通过
     */
    FAILED(3, "审核未通过"),
    /**
     * 文章撤回
     */
    WITHDRAW(4, "文章撤回");



    private final Integer val;
    private final String desc;

    /**
     * 判断传入的审核状态是不是有效的值
     *
     * @param value 文章状态
     * @return boolean -> 文章状态是否有效
     */
    public static boolean isValidArticleStatus(Integer value) {
        if (value != null) {
            return value.equals(REVIEWING.getVal())
                    || value.equals(APPROVED.getVal())
                    || value.equals(FAILED.getVal())
                    || value.equals(WITHDRAW.getVal());
        }
        return false;
    }
}
