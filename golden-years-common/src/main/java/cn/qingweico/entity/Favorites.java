package cn.qingweico.entity;

import cn.qingweico.enums.FavoritesType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏夹
 *
 * @author zqw
 * @date 2022/4/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class Favorites extends BaseEntity {

    /**
     * 收藏夹名称
     */
    private String name;

    /**
     * 所属用户id
     */
    private String userId;

    /**
     * 收藏夹中包含的文章id
     */
    private String articles;

    /**
     * 收藏夹描述
     */
    private String description;

    /**
     * 收藏夹是否公开 {@link FavoritesType}
     */
    private Integer open;
}
