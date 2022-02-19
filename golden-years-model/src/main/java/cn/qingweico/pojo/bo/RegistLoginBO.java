package cn.qingweico.pojo.bo;

import javax.validation.constraints.NotBlank;

/**
 * @author:qiming
 * @date: 2021/9/6
 */
public class RegistLoginBO {
   @NotBlank(message = "手机号不能为空")
   private String mobile;
   @NotBlank(message = "验证码不能为空")
   private String smsCode;

   public String getMobile() {
      return mobile;
   }

   public void setMobile(String mobile) {
      this.mobile = mobile;
   }

   public String getSmsCode() {
      return smsCode;
   }

   public void setSmsCode(String smsCode) {
      this.smsCode = smsCode;
   }

   @Override
   public String toString() {
      return "RegistLoginBO{" +
              "mobile='" + mobile + '\'' +
              ", smsCode='" + smsCode + '\'' +
              '}';
   }
}
