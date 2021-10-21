package cn.qingweico.pojo.bo;

import javax.validation.constraints.NotBlank;

/**
 * @author:qiming
 * @date: 2021/10/21
 */
public class PasswordAuthBO {
   @NotBlank(message = "认证信息不能为空")
   private String auth;
   @NotBlank(message = "密码不能为空")
   private String password;

   public String getAuth() {
      return auth;
   }

   public void setAuth(String auth) {
      this.auth = auth;
   }

   public String getPassword() {
      return password;
   }

   @Override
   public String toString() {
      return "PasswordAuthBO{" +
              "auth='" + auth + '\'' +
              ", password='" + password + '\'' +
              '}';
   }

   public void setPassword(String password) {
      this.password = password;
   }

}
