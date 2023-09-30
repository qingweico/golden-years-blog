package cn.qingweico.entity.model;

import cn.qingweico.entity.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Data
public class ArticleDetail {
    private String id;
    private String title;
    private String articleCover;
    private String categoryId;
    private String authorId;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String content;
    private String authorFace;
    private String authorName;
    private Integer readCounts;
    private Integer collectCounts;
    private Integer commentCounts;
    private Integer starCounts;
    private List<Tag> tagList;
}
