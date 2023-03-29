package cn.qingweico.pojo.bo;

import lombok.Data;

import java.util.Date;

/**
 * 添加SysUser的BO
 *
 * @author zqw
 * @date 2021/9/9
 */
@Data
public class OperatorSysUserBO {
    private String id;
    private String username;
    private String password;
    private String confirmPassword;
    private String img64;
    private String faceId;
    private String avatar;
    private String mobile;
    private String email;
    private Date birthday;
    private Integer gender;
}
