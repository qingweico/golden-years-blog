package cn.qingweico.entity.model;


import lombok.Data;

import java.util.Date;

/**
 * @author zqw
 * @date 2022/4/16
 */

@Data
public class MineFavorites {
    private String name;
    private String userId;
    private Integer open;
    private String description;
    /**
     * 该收藏夹中是否收藏了该文章
     */
    private Boolean collectedOrNot;
    /**
     * 该收藏夹中收藏的文章数量
     */
    private Integer inFavorites;
    /**
     * 收藏夹创建时间
     */
    private Date createTime;
    /**
     * 收藏夹更新时间
     */
    private Date updateTime;
}
