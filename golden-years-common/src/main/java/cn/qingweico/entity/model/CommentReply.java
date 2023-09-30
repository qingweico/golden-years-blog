package cn.qingweico.entity.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Data
public class CommentReply {
    @NotBlank
    private String articleId;
    @NotBlank
    private String userId;
    private Integer likes;
    private String content;
    private String parent;
}
