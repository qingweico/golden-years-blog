package cn.qingweico.entity.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2023/9/27
 */
@Data
public class SaveOrUpdateCategory {
    private String id;
    @NotBlank(message = "分类名不能为空")
    private String name;
    private String oldName;
    private String description;
    private Integer status;
}
