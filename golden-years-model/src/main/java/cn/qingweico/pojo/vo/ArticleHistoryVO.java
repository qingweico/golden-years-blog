package cn.qingweico.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2022/5/6
 */
@Data
public class ArticleHistoryVO {
    private String articleId;
    private String userId;
    private String articleName;
    private Date browseTime;
    private String articleAuthorName;
    private String articleAuthorFace;
    private Integer articleReadCounts;
    private Integer articleCollectCounts;
    private Integer articleStarCounts;
    private Integer articleCommentCounts;
}
