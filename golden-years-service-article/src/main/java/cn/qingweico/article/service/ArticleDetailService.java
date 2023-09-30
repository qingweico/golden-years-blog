package cn.qingweico.article.service;

import cn.qingweico.entity.Favorites;
import cn.qingweico.entity.model.CollectArticle;


import java.util.List;

/**
 * @author zqw
 * @date 2022/4/16
 */
public interface ArticleDetailService {
    /**
     * 收藏文章
     *
     * @param collectArticle {@link CollectArticle}
     */
    void collectArticle(CollectArticle collectArticle);

    /**
     * 取消收藏文章
     *
     * @param collectArticle {@link CollectArticle}
     * @return 取消收藏是否成功
     */
    boolean cancelCollectArticle(CollectArticle collectArticle);

    /**
     * 创建收藏夹
     *
     * @param favorites {@link Favorites}
     */
    void createFavorites(Favorites favorites);

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
     * @param favorites {@link Favorites}
     */
    void editFavorites(Favorites favorites);

    /**
     * 删除收藏夹
     *
     * @param collectArticle {@link CollectArticle}
     */
    void deleteFavorites(CollectArticle collectArticle);
}
