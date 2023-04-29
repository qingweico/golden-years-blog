package cn.qingweico.admin.model.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/7
 */
@Data
public class UserInfoBO {
    private String id;

    @NotBlank(message = "用户昵称不能为空")
    @Length(max = 20, message = "用户昵称不能超过20位")
    private String nickname;

    @NotBlank(message = "用户头像不能为空")
    private String face;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @Email
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotNull(message = "请选择一个性别")
    @Min(value = 0, message = "性别选择不正确")
    @Max(value = 2, message = "性别选择不正确")
    private Integer sex;

    @NotNull(message = "请选择生日日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date birthday;

    @NotBlank(message = "请选择所在城市")
    private String province;
}
