package cn.qingweico.pojo.eo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/19
 */
@Data
@Document(indexName = "articles", type = "_doc")
public class ArticleEo {
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
   private String summary;
   @Field
   private String author;
   @Field
   private Date createTime;

}