package cn.qingweico.pojo;

import lombok.Data;

import javax.persistence.Id;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Data
public class Fans {
    @Id
    private String id;

    /**
     * 博主id
     */
    private String writerId;

    /**
     * 粉丝用户id
     */
    private String fanId;

    /**
     * 粉丝头像
     */
    private String face;

    /**
     * 粉丝昵称
     */
    private String fanNickname;

    /**
     * 粉丝性别
     */
    private Integer sex;

    /**
     * 省份
     */
    private String province;
}