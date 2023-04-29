package cn.qingweico.admin.model.vo;

import cn.qingweico.pojo.Tag;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2022/4/2
 */
@Data
public class ArticleArchiveVO {
    private String articleId;
    private String categoryId;
    private String title;
    private Date createTime;
    private List<Tag> tagList;
}
