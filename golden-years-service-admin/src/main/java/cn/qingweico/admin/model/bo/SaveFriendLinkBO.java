package cn.qingweico.admin.model.bo;

import cn.qingweico.validate.CheckUrl;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Data
public class SaveFriendLinkBO {
   private String id;
   @NotBlank(message = "友情链接名不能为空")
   private String linkName;
   @NotBlank(message = "友情链接地址不能为空")
   @CheckUrl
   private String linkUrl;
   @NotNull(message = "请选择保留或删除")
   private Integer isDelete;
}
