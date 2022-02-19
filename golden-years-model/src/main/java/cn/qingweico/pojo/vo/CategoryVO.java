package cn.qingweico.pojo.vo;

/**
 * @author:qiming
 * @date: 2021/10/21
 */
public class CategoryVO {
    private Integer id;
    private String name;
    private String tagColor;
    private Integer eachCategoryArticleCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    public Integer getEachCategoryArticleCount() {
        return eachCategoryArticleCount;
    }

    public void setEachCategoryArticleCount(Integer eachCategoryArticleCount) {
        this.eachCategoryArticleCount = eachCategoryArticleCount;
    }
}
