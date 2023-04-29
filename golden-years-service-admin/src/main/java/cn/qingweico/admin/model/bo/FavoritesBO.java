package cn.qingweico.admin.model.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2022/4/17
 */
@Data
public class FavoritesBO {
    @NotBlank
    private String id;
    private String name;
    private Integer open;
    private String description;
}
