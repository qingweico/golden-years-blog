package cn.qingweico.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2021/10/21
 */
@Data
public class CategoryVO {
    private String id;
    private String name;
    private Date createTime;
    private String description;
    private Integer eachCategoryArticleCount;
}
