package cn.qingweico.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Data
@Table(name = "t_fans")
public class Fans {
    @Id
    private String id;

    /**
     * 关注的作者
     */
    private String author;

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