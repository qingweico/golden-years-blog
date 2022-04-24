package cn.qingweico.pojo;

import cn.qingweico.enums.FavoritesType;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 收藏夹
 *
 * @author zqw
 * @date 2022/4/15
 */
@Data
@Table(name = "t_collect")
public class Favorites {
    @Id
    private String id;

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

    /**
     * 收藏夹创建时间
     */
    private Date createTime;
    /**
     * 收藏夹更新时间
     */
    private Date updateTime;
}
