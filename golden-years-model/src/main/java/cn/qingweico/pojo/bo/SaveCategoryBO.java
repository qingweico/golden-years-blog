package cn.qingweico.pojo.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Data
public class SaveCategoryBO {
   private String id;
   @NotBlank(message = "分类名不能为空")
   private String name;
   private String oldName;
   private String description;
   private Integer status;
}