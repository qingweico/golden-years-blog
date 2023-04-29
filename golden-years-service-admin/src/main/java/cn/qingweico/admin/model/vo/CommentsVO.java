package cn.qingweico.admin.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/14
 */
@Data
public class CommentsVO {
    private String commentId;
    private String content;
    private Boolean liked;
    private Integer likes;
    private String replyName;
    private String replyAvatar;
    private String visitorName;
    private String visitorAvatar;
    private CommentsVO childrenComments;
    private Date createAt;
}
