package cn.qingweico.entity;

import cn.qingweico.enums.LoginType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 主站配置
 *
 * @author zqw
 * @date 2022/4/11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class WebConfig extends BaseEntity {

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
     * 网站标题
     */
    private String title;
    /**
     * 登录方式 {@link LoginType}
     */
    private String loginTypeList;
}
