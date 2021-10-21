package cn.qingweico.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author:qiming
 * @date: 2021/9/13
 */
public class ArticleDetailVO {

    private String id;
    private String title;
    private String cover;
    private Integer categoryId;
    private String categoryName;
    private String publishUserId;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;
    private String publishUserFace;
    private String content;

    private String publishUserName;
    private Integer readCounts;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPublishUserId() {
        return publishUserId;
    }


    public void setPublishUserId(String publishUserId) {
        this.publishUserId = publishUserId;
    }

    public String getPublishUserName() {
        return publishUserName;
    }

    public void setPublishUserName(String publishUserName) {
        this.publishUserName = publishUserName;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getContent() {
        return content;
    }

    public void setPublishUserFace(String PublishUserFace) {
        this.publishUserFace = PublishUserFace;
    }

    public String getPublishUserFace() {
        return publishUserFace;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getReadCounts() {
        return readCounts;
    }

    public void setReadCounts(Integer readCounts) {
        this.readCounts = readCounts;
    }
}