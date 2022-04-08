package cn.qingweico.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zqw
 * @date 2022/3/23
 */
@Data
@Table(name = "t_role")
public class Role {

    @Id
    private String id;


    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 介绍
     */
    private String summary;

    /**
     * 该角色所能操控的菜单范围
     */
    private String categoryMenuIds;
    /**
     * 状态 0: 禁用  1: 启用
     */
    private int status;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
