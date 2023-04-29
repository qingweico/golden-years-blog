package cn.qingweico.admin.model.vo;

import cn.qingweico.pojo.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2022/4/10
 */
@Data
public class ArticleAdminVO {
    private String id;
    private String title;
    private String articleCover;
    private String categoryId;
    private String authorId;
    private Integer articleStatus;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String content;
    private String authorFace;
    private String authorName;
    private Integer followCounts;
    private Integer fansCounts;
    private Integer readCounts;
    private Integer collectCounts;
    private Integer commentCounts;
    private Integer isDelete;
    private List<Tag> tagList;
}
