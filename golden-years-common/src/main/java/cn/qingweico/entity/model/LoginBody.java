package cn.qingweico.entity.model;

import lombok.Data;

/**
 * 用户登录对象
 *
 * @author zqw
 * @date 2023-09-23
 */
@Data
public class LoginBody
{
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 人脸 base64
     */
    private String img64;

    /**
     * 唯一标识
     */
    private String uuid;
}
