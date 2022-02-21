package cn.qingweico.pojo.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2021/10/21
 */
@Data
public class PasswordAuthBO {
   @NotBlank(message = "认证信息不能为空")
   private String auth;
   @NotBlank(message = "密码不能为空")
   private String password;
}
