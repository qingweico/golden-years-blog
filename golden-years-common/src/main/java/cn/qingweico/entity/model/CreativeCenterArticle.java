package cn.qingweico.entity.model;

import cn.qingweico.entity.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2022/4/10
 */
@Data
public class CreativeCenterArticle {
    private String id;
    private String title;
    private Integer articleType;
    private String articleCover;
    private Integer articleStatus;
    private String categoryId;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String content;
    private Integer readCounts;
    private Integer collectCounts;
    private Integer commentCounts;
}
