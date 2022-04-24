package cn.qingweico.pojo.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2022/4/16
 */
@Data
public class CollectBO {
    /**
     * 收藏夹id
     */
    private String favoritesId;
    private String name;
    @NotBlank
    private String userId;
    private String articleId;
    private String description;
    private Integer open;
}
