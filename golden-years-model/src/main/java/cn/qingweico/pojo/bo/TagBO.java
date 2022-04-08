package cn.qingweico.pojo.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2022/3/29
 */
@Data
public class TagBO {
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String color;
    private Integer status;
}
