package cn.qingweico.pojo.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2021/9/6
 */
@Data
public class RegistLoginBO {
   @NotBlank(message = "手机号不能为空")
   private String mobile;
   @NotBlank(message = "验证码不能为空")
   private String smsCode;
}
