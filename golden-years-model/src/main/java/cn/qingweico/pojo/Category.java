package cn.qingweico.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Data
@Table(name = "t_category")
public class Category {
   @Id
   private Integer id;

   /**
    * 文章分类名称
    */
   private String name;

   /**
    * 标签颜色
    */
   private String tagColor;
}