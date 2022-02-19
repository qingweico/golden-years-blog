package cn.qingweico.pojo;

import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/11
 */
@Data
public class Article {
   @Id
   private String id;

   /**
    * 文章标题
    */
   private String title;
   /**
    * 文章内容
    */
   private String content;
   /**
    * 文章所属分类id
    */
   private Integer categoryId;

   /**
    * 文章类型; 1: 图文(1张封面), 2: 纯文字
    */
   private Integer articleType;

   /**
    * 文章封面图
    */
   private String articleCover;

   /**
    * 是否是预约定时发布的文章; 1: 预约(定时)发布, 0: 即时发布(在预约时间到点的时候, 把1改为0, 则发布)
    */
   private Integer isAppoint;

   /**
    * 文章状态; 1: 审核中(用户已提交)
    *          2: 人工审核中
    *          3: 审核通过(已发布)
    *          4: 审核未通过
    *          5: 文章撤回(已发布的情况下才能撤回和删除)
    */
   private Integer articleStatus;

   /**
    * 文章发布者id
    */
   private String publishUserId;

   /**
    * 文章发布时间(也是预约发布的时间)
    */
   private Date publishTime;

   /**
    * 用户累计点击阅读数(喜欢数)(点赞) - 放redis
    */
   private Integer readCounts;
   /**
    * 文章收藏量 - 放redis
    */
   private Integer collectCounts;
   /**
    * 文章评论总数; 评论防刷, 距离上次评论需要间隔时间控制数秒
    */
   private Integer commentCounts;

   /**
    * 文章静态化与mongo关联id
    */
   private String mongoFileId;

   /**
    * 逻辑删除状态; 非物理删除, 1: 删除, 0: 未删除
    */
   private Integer isDelete;

   /**
    * 文章的创建时间
    */
   private Date createTime;

   /**
    * 文章的修改时间
    */
   private Date updateTime;
   /**
    * 文章的影响力
    */
   private Integer influence;
}