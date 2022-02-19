package cn.qingweico.pojo.eo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Date;

/**
 * @author:qiming
 * @date: 2021/9/19
 */
@Document(indexName = "articles", type = "_doc")
public class ArticleEO {
   @Id
   private String id;
   @Field
   private String title;
   @Field
   private Integer categoryId;
   @Field
   private Integer articleType;
   @Field
   private String articleCover;
   @Field
   private String brief;
   @Field
   private String publishUserId;
   @Field
   private Date publishTime;

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

   public Integer getArticleType() {
      return articleType;
   }

   public void setArticleType(Integer articleType) {
      this.articleType = articleType;
   }

   public String getArticleCover() {
      return articleCover;
   }

   public void setArticleCover(String articleCover) {
      this.articleCover = articleCover;
   }

   public String getPublishUserId() {
      return publishUserId;
   }

   public void setPublishUserId(String publishUserId) {
      this.publishUserId = publishUserId;
   }

   public Date getPublishTime() {
      return publishTime;
   }

   public void setPublishTime(Date publishTime) {
      this.publishTime = publishTime;
   }

   public String getBrief() {
      return brief;
   }

   public void setBrief(String brief) {
      this.brief = brief;
   }
}