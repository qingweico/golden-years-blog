package cn.qingweico.entity.model;

import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2022/4/2
 */
@Data
public class ArticleArchive {
    private String articleId;
    private String categoryId;
    private String title;
    private Date createTime;
}
