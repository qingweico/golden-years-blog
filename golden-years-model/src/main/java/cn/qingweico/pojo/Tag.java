package cn.qingweico.pojo;

import lombok.Data;

import javax.persistence.Table;

/**
 * @author zqw
 * @date 2022/3/3
 */
@Data
@Table(name = "t_tag")
public class Tag {
    private String id;
    /**
     * 标签名称
     */
    private String name;
    /**
     * 标签状态
     */
    private String status;
    /**
     * 标签颜色
     */
    private String color;

    /**
     * 个人自定义标签 - 标签用户id标识
     */
    private String userId;
    /**
     * 系统标签 - 默认为1; 否则为用户自定义标签
     */
    private Integer sys;
}