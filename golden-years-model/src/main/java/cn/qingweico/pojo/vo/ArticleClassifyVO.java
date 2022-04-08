package cn.qingweico.pojo.vo;

import cn.qingweico.pojo.Tag;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2022/4/2
 */
@Data
public class ArticleClassifyVO {
    private String articleId;
    private String title;
    private String categoryId;
    private Date createTime;
    private List<Tag> tagList;
}
