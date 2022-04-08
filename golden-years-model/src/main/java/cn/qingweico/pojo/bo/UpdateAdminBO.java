package cn.qingweico.pojo.bo;

import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2022/3/30
 */
@Data
public class UpdateAdminBO {
    private  String id;
    private String username;
    private String avatar;
    private String mobile;
    private String email;
    private Date birthday;
    private Integer gender;
}
