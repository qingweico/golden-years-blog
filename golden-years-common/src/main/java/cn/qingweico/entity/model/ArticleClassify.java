package cn.qingweico.entity.model;


import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2022/4/2
 */
@Data
public class ArticleClassify {
    private String articleId;
    private String title;
    private String categoryId;
    private Date createTime;
}
