package cn.qingweico.pojo.vo;

import lombok.Data;

/**
 * @author zqw
 * @date 2021/10/21
 */
@Data
public class CategoryVO {
    private Integer id;
    private String name;
    private String tagColor;
    private Integer eachCategoryArticleCount;
}
