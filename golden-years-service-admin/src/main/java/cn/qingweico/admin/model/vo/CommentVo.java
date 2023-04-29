package cn.qingweico.admin.model.vo;

import cn.qingweico.admin.model.bo.Reply;
import cn.qingweico.admin.model.bo.Visitor;
import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2022/4/9
 */
@Data
public class CommentVo {
    private String commentId;
    private String content;
    private Boolean liked;
    private Integer likes;
    private Visitor visitor;
    private Reply reply;
    private CommentsVO childrenComments;
    private Date createAt;
}
