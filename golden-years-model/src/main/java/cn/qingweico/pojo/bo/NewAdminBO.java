package cn.qingweico.pojo.bo;

import lombok.Data;

/**
 * 添加管理员的BO
 *
 * @author zqw
 * @date 2021/9/9
 */
@Data
public class NewAdminBO {

    private String username;
    private String password;
    private String confirmPassword;
    private String img64;
    private String faceId;
}
