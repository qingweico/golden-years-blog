package cn.qingweico.enums;

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
public enum ArticleReviewStatus {
    /**
     * 审核中(用户已提交)
     */
    REVIEWING(1, "审核中(用户已提交)"),

    /**
     * 审核通过(已发布)
     */
    SUCCESS(3, "审核通过(已发布)"),
    /**
     * 审核未通过
     */
    FAILED(4, "审核未通过"),
    /**
     * 文章撤回
     */
    WITHDRAW(5, "文章撤回");


    public final Integer type;
    public final String value;

    ArticleReviewStatus(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * 判断传入的审核状态是不是有效的值
     *
     * @param tempStatus 文章状态
     * @return boolean -> 文章状态是否有效
     */
    public static boolean isArticleStatusValid(Integer tempStatus) {
        if (tempStatus != null) {
            return tempStatus.equals(REVIEWING.type)
                    || tempStatus.equals(SUCCESS.type)
                    || tempStatus.equals(FAILED.type)
                    || tempStatus.equals(WITHDRAW.type);
        }
        return false;
    }
}
