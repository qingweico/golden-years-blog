package cn.qingweico.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Data
public class ArticleDetailVO {

    private String id;
    private String title;
    private String cover;
    private Integer categoryId;
    private String categoryName;
    private String author;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String authorFace;
    private String content;
    private String authorName;
    private Integer readCounts;
    private Integer collectCounts;
}