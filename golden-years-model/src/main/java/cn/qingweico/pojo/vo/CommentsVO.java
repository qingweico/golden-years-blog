package cn.qingweico.pojo.vo;

import cn.qingweico.pojo.bo.Reply;
import lombok.Data;
import org.apache.ibatis.javassist.compiler.ast.Visitor;

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