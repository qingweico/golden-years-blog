package cn.qingweico.entity.model;

import cn.qingweico.util.validate.CheckUrl;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Data
public class SaveOrUpdateFriendLink {
    private String id;
    @NotBlank(message = "友情链接名不能为空")
    private String linkName;
    @NotBlank(message = "友情链接地址不能为空")
    @CheckUrl
    private String linkUrl;;
}
