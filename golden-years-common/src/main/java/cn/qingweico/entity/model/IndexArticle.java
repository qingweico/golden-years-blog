package cn.qingweico.entity.model;

import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/12
 */
@Data
public class IndexArticle {
    private String id;
    private String title;
    private String categoryId;
    private String articleCover;
    private Integer readCounts;
    private Integer collectCounts;
    private Integer commentCounts;
    private Integer starCounts;
    private Date createTime;
    private String summary;
    private UserBasicInfo authorVO;
}
