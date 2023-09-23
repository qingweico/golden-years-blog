package cn.qingweico.entity.model;

import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/7
 */
@Data
public class UserAccountInfoVO {
    private String id;
    private String mobile;
    private String nickname;
    private String face;
    private String realName;
    private String email;
    private Integer sex;
    private Date birthday;
    private String province;
    private String city;
    private String district;
}
