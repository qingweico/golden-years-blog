package cn.qingweico.pojo;

import cn.qingweico.enums.LoginType;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zqw
 * @date 2022/4/11
 */
@Data
@Table(name = "t_web_config")
public class WebConfig {
    @Id
    private String id;

    /**
     * 网站logo
     */
    private String logo;

    /**
     * 网站名称
     */
    private String name;

    /**
     * 网站介绍
     */
    private String description;

    /**
     * 网站作者
     */
    private String author;
    /**
     * 网站备案号
     */
    private String recordNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 网站标题
     */
    private String title;
    /**
     * 登录方式 {@link LoginType}
     */
    private String loginTypeList;

}
