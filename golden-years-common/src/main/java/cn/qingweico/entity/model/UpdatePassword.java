package cn.qingweico.entity.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2022/3/30
 */
@Data
public class UpdatePassword {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}
