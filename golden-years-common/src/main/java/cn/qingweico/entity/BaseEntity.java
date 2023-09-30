package cn.qingweico.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity基类
 *
 * @author zqw
 * @date 2023-04-04
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true, value ={"createdBy", "created", "lastUpdBy", "lastUpd"})
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -3748314123956389002L;
    /**
     * 主键
     **/
    private String id;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @EqualsAndHashCode.Exclude
    private String updateBy;

    /**
     * 更新时间
     */
    @EqualsAndHashCode.Exclude
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
