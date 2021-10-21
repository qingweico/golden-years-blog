package cn.qingweico.enums;

/**
 * 文章审核状态
 * 文章状态:
 * 1 ---> 审核中(用户已提交)
 * 2 ---> 机审结束 等待人工审核
 * 3 ---> 审核通过
 * 4 ---> 审核不通过
 * 5 ---> 文章撤回
 *
 * @author:qiming
 * @date: 2021/9/11
 */
public enum ArticleReviewStatus {
    REVIEWING(1, "审核中(用户已提交)"),

    WAITING_MANUAL(2, "机审结束, 等待人工审核"),

    SUCCESS(3, "审核通过(已发布)"),

    FAILED(4, "审核未通过"),

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
     * @return boolean
     */
    public static boolean isArticleStatusValid(Integer tempStatus) {
        if (tempStatus != null) {
            return tempStatus.equals(REVIEWING.type)
                    || tempStatus.equals(WAITING_MANUAL.type)
                    || tempStatus.equals(SUCCESS.type)
                    || tempStatus.equals(FAILED.type)
                    || tempStatus.equals(WITHDRAW.type);
        }
        return false;
    }
}
