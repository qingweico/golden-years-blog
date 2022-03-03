package cn.qingweico.pojo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/11
 */
@Data
public class NewArticleBO {

   @NotBlank(message = "文章标题不能为空")
   @Length(max = 30, message = "文章标题长度不能超过30")
   private String title;

   @NotBlank(message = "文章内容不能为空")
   private String content;

   @NotNull(message = "请选择文章类别")
   private Integer categoryId;

   @NotNull(message = "请选择正确的文章封面类型")
   @Min(value = 1, message = "请选择正确的文章封面类型")
   @Max(value = 2, message = "请选择正确的文章封面类型")
   private Integer articleType;
   private String articleCover;

   @NotNull(message = "文章发布类型不正确")
   @Min(value = 0, message = "文章发布类型不正确")
   @Max(value = 1, message = "文章发布类型不正确")
   private Integer isAppoint;

   @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
   private Date createTime;

   @NotBlank(message = "用户未登录")
   private String authorId;
}