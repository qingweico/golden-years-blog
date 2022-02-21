package cn.qingweico.pojo.bo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Data
public class CommentReplyBO {

    @NotBlank(message = "留言信息不完整")
    private String articleId;

    @NotBlank(message = "留言信息不完整")
    private String fatherId;

    @NotBlank(message = "当前用户信息不正确, 请尝试重新登录")
    private String commentUserId;

    @NotBlank(message = "留言内容不能为空")
    @Length(max = 50, message = "文章内容长度不能超过50")
    private String content;
}