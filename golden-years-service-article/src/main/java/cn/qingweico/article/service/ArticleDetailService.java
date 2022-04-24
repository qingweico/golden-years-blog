package cn.qingweico.article.service;

import cn.qingweico.pojo.Favorites;
import cn.qingweico.pojo.bo.CollectBO;
import cn.qingweico.pojo.bo.FavoritesBO;

import java.util.List;

/**
 * @author zqw
 * @date 2022/4/16
 */
public interface ArticleDetailService {
    /**
     * 收藏文章
     *
     * @param collectBO {@link Favorites}
     */
    void collectArticle(CollectBO collectBO);

    /**
     * 取消收藏文章
     *
     * @param collectBO {@link Favorites}
     * @return 取消收藏是否成功
     */
    boolean cancelCollectArticle(CollectBO collectBO);

    /**
     * 创建收藏夹
     *
     * @param collectBO {@link Favorites}
     */
    void createFavorites(CollectBO collectBO);

    /**
     * 根据用户id获取我的收藏夹
     *
     * @param userId 用户id
     * @return List<Favorites>
     */
    List<Favorites> getFavoritesByUserId(String userId);


    /**
     * 修改收藏夹信息
     *
     * @param favoritesBO {@link FavoritesBO}
     */
    void editFavorites(FavoritesBO favoritesBO);

    /**
     * 删除收藏夹
     *
     * @param collectBO {@link CollectBO}
     */
    void deleteFavorites(CollectBO collectBO);
}
