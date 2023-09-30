package cn.qingweico.article.service.impl;

import cn.qingweico.core.service.BaseService;
import cn.qingweico.article.mapper.FavoritesMapper;
import cn.qingweico.article.service.ArticleDetailService;
import cn.qingweico.entity.Favorites;
import cn.qingweico.entity.model.CollectArticle;
import cn.qingweico.enums.FavoritesType;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConst;
import cn.qingweico.global.RedisConst;
import cn.qingweico.result.Response;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.SnowflakeIdWorker;
import cn.qingweico.util.redis.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zqw
 * @date 2022/4/16
 */
@Slf4j
@Service
public class ArticleDetailServiceImpl extends BaseService implements ArticleDetailService {
    @Resource
    private FavoritesMapper favoritesMapper;

    @Resource
    private RedisCache redisCache;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void collectArticle(CollectArticle collectArticle) {

        String favoritesId = collectArticle.getFavoritesId();
        if (StringUtils.isNotBlank(favoritesId)) {
            // 收藏文章
            Favorites favorites = favoritesMapper.selectById(favoritesId);
            String articles = favorites.getArticles();
            String pendingAddArticles = collectArticle.getArticleId();
            StringBuilder builder = new StringBuilder();
            // 收藏夹中暂无文章
            if (StringUtils.isBlank(articles)) {
                builder.append("[");
                builder.append("\"");
                builder.append(pendingAddArticles);
                builder.append("\"");
                builder.append("]");
            } else {
                builder = new StringBuilder(articles);
                // 收藏夹有文章
                builder.delete(builder.length() - 1, builder.length());
                builder.append(",");
                builder.append("\"");
                builder.append(pendingAddArticles);
                builder.append("\"");
                builder.append("]");
            }
            favorites.setArticles(builder.toString());
            if (favoritesMapper.updateById(favorites) < 0) {
                GraceException.error(Response.SYSTEM_OPERATION_ERROR);
            }
        }
    }

    @Override
    public boolean cancelCollectArticle(CollectArticle collectArticle) {
        String favoritesId = collectArticle.getFavoritesId();
        if (StringUtils.isNotBlank(favoritesId)) {
            Favorites favorites = favoritesMapper.selectById(favoritesId);
            String articles = favorites.getArticles();
            String pendingCancelCollectArticle = collectArticle.getArticleId();
            String[] articleIds = articles.replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",");
            List<String> list = Arrays.stream(articleIds).collect(Collectors.toList());
            boolean result = list.remove(pendingCancelCollectArticle);
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (String articleId : list) {
                builder.append("\"");
                builder.append(articleId);
                builder.append("\",");
            }
            builder.append("]");
            builder.delete(builder.length() - 2, builder.length() - 1);
            favorites.setArticles(builder.toString());
            int updated = favoritesMapper.updateById(favorites);
            return result && updated == 1;
        }
        return false;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void createFavorites(Favorites params) {
        String id = SnowflakeIdWorker.nextId();
        Favorites favorites = Favorites.builder().build();
        favorites.setId(id);
        String favoritesDesc = params.getDescription();
        if (StringUtils.isNotBlank(favoritesDesc)) {
            favorites.setDescription(params.getDescription());
        } else {
            favorites.setDescription("这是一个默认的收藏夹 在创建用户时创建的。默认是公开的");
        }
        favorites.setUserId(params.getUserId());
        String favoritesName = params.getName();
        if (StringUtils.isNotBlank(favoritesName)) {
            favorites.setName(favoritesName);
        } else {
            favorites.setName("默认收藏夹");
        }
        favorites.setArticles(SysConst.EMPTY_STRING);
        Integer isOpen = params.getOpen();
        if (isOpen == null) {
            favorites.setOpen(FavoritesType.PUBLIC.getVal());
        } else {
            favorites.setOpen(isOpen);
        }
        favorites.setCreateTime(DateUtils.nowDateTime());
        favorites.setUpdateTime(DateUtils.nowDateTime());
        favoritesMapper.insert(favorites);
    }

    @Override
    public List<Favorites> getFavoritesByUserId(String userId) {
        LambdaQueryWrapper<Favorites> lwq = new LambdaQueryWrapper<>();
        lwq.eq(Favorites::getUserId, userId);
        return favoritesMapper.selectList(lwq);
    }

    @Override
    public void editFavorites(Favorites favorites) {
        if (favoritesMapper.updateById(favorites) < 0) {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void deleteFavorites(CollectArticle collectArticle) {
        String favoritesId = collectArticle.getFavoritesId();
        Favorites dbFavorites = favoritesMapper.selectById(favoritesId);
        String userId = dbFavorites.getUserId();
        String articles = dbFavorites.getArticles();
        String[] articleIds = articles.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(",");
        for (String articleId : articleIds) {
            // 设置用户收藏标志位(未收藏该文章)
            redisCache.del(RedisConst.REDIS_ARTICLE_ALREADY_COLLECT +
                    SysConst.SYMBOL_COLON + articleId +
                    SysConst.SYMBOL_HYPHEN + userId +
                    SysConst.SYMBOL_HYPHEN + favoritesId);
            // 文章收藏数减少
            redisCache.decrement(RedisConst.REDIS_ARTICLE_COLLECT_COUNTS + SysConst.SYMBOL_COLON + articleId, 1);
            // 删除收藏夹的收藏量
            redisCache.del(RedisConst.REDIS_ARTICLE_COLLECT_COUNTS +
                    SysConst.SYMBOL_COLON + userId +
                    SysConst.SYMBOL_HYPHEN + favoritesId);
        }
        if (favoritesMapper.deleteById(favoritesId) < 0) {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }
}
