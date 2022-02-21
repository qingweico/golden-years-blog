package cn.qingweico.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/12
 */
@Data
public class IndexArticleVO {
    private String id;
    private String title;
    private Integer categoryId;
    private Integer articleType;
    private String articleCover;
    private Integer isAppoint;
    private Integer articleStatus;
    private String author;
    private Integer readCounts;
    private Integer commentCounts;
    private String mongoFileId;
    private Integer isDelete;
    private Date createTime;
    private Date updateTime;
    private String summary;
    private UserBasicInfoVO authorVO;
}