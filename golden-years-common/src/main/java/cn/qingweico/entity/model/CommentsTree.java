package cn.qingweico.entity.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/14
 */
@Data
public class CommentsTree {
    private String commentId;
    private String content;
    private Boolean liked;
    private Integer likes;
    private List<CommentsTree> children;
    private Date createAt;
}
