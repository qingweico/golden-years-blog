package cn.qingweico.admin.model.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Data
public class CommentReplyBO {
    @NotBlank
    private String articleId;
    @NotBlank
    private String commentUserId;
    private String commentUserNickname;
    private String commentUserFace;
    private String imgSrc;
    private Integer likes;
    private String content;
    private String parent;
}
