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
    private String categoryId;
    private String articleCover;
    private Integer readCounts;
    private Integer collectCounts;
    private Integer commentCounts;
    private Date createTime;
    private Date updateTime;
    private String summary;
    private UserBasicInfoVO authorVO;
}