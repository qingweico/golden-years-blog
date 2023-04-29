package cn.qingweico.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 权限相关
 *
 * @author zqw
 * @date 2023/2/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class Permission extends BaseEntity {
    /**
     * 权限编码
     */
    private String code;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限动作
     */
    private String action;

    /**
     * 状态 0: 禁用  1: 启用
     */
    private int status;
}
