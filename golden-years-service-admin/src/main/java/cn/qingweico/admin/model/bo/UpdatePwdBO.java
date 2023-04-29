package cn.qingweico.admin.model.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2022/3/30
 */
@Data
public class UpdatePwdBO {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}
