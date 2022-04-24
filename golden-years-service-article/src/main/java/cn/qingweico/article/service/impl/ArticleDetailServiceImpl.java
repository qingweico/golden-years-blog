package cn.qingweico.article.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.FavoritesMapper;
import cn.qingweico.article.service.ArticleDetailService;
import cn.qingweico.enums.FavoritesType;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConf;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.Favorites;
import cn.qingweico.pojo.bo.CollectBO;
import cn.qingweico.pojo.bo.FavoritesBO;
import cn.qingweico.result.ResponseStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

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

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void collectArticle(CollectBO collectBO) {

        String favoritesId = collectBO.getFavoritesId();
        if (StringUtils.isNotBlank(favoritesId)) {
            // 收藏文章
            Favorites favorites = favoritesMapper.selectByPrimaryKey(favoritesId);
            String articles = favorites.getArticles();
            String pendingAddArticles = collectBO.getArticleId();
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
            if (favoritesMapper.updateByPrimaryKeySelective(favorites) < 0) {
                log.error("collect article error");
                GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
            }
        }
    }

    @Override
    public boolean cancelCollectArticle(CollectBO collectBO) {
        String favoritesId = collectBO.getFavoritesId();
        if (StringUtils.isNotBlank(favoritesId)) {
            Favorites favorites = favoritesMapper.selectByPrimaryKey(favoritesId);
            String articles = favorites.getArticles();
            String pendingCancelCollectArticle = collectBO.getArticleId();
            String[] articleIds = articles.replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",");
            List<String> list = Arrays.stream(articleIds).collect(Collectors.toList());
            boolean result = list.remove(pendingCancelCollectArticle);
            if (!result) {
                log.error("remove article error");
            }
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
            int updated = favoritesMapper.updateByPrimaryKeySelective(favorites);
            return result && updated == 1;
        }
        return false;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void createFavorites(CollectBO collectBO) {
        String id = sid.nextShort();
        Favorites favorites = new Favorites();
        favorites.setId(id);
        String favoritesDesc = collectBO.getDescription();
        if (StringUtils.isNotBlank(favoritesDesc)) {
            favorites.setDescription(collectBO.getDescription());
        } else {
            favorites.setDescription("");
        }
        favorites.setUserId(collectBO.getUserId());
        String favoritesName = collectBO.getName();
        if (StringUtils.isNotBlank(favoritesName)) {
            favorites.setName(favoritesName);
        } else {
            favorites.setName("默认收藏夹");
        }
        favorites.setArticles("");
        Integer isOpen = collectBO.getOpen();
        if (isOpen == null) {
            favorites.setOpen(FavoritesType.PUBLIC.type);
        } else {
            favorites.setOpen(isOpen);
        }
        favorites.setCreateTime(new Date());
        favorites.setUpdateTime(new Date());
        if (favoritesMapper.insert(favorites) < 0) {
            log.error("create favorites error");
            GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public List<Favorites> getFavoritesByUserId(String userId) {
        Example example = new Example(Favorites.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        return favoritesMapper.selectByExample(example);
    }

    @Override
    public void editFavorites(FavoritesBO favoritesBO) {
        Favorites favorites = new Favorites();
        BeanUtils.copyProperties(favoritesBO, favorites);
        favorites.setUpdateTime(new Date());
        if (favoritesMapper.updateByPrimaryKeySelective(favorites) < 0) {
            log.error("update favorites error");
            GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);

        }
    }

    @Override
    public void deleteFavorites(CollectBO collectBO) {
        String favoritesId = collectBO.getFavoritesId();
        Favorites favorites = favoritesMapper.selectByPrimaryKey(favoritesId);
        String userId = collectBO.getUserId();
        String articles = favorites.getArticles();
        String[] articleIds = articles.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(",");
        for (String articleId : articleIds) {
            // 设置用户收藏标志位(未收藏该文章)
            redisOperator.del(RedisConf.REDIS_ARTICLE_ALREADY_COLLECT +
                    SysConf.SYMBOL_COLON + articleId +
                    SysConf.SYMBOL_HYPHEN + userId +
                    SysConf.SYMBOL_HYPHEN + favoritesId);
            // 文章收藏数减少
            redisOperator.decrement(RedisConf.REDIS_ARTICLE_COLLECT_COUNTS + SysConf.SYMBOL_COLON + articleId, 1);
            // 删除收藏夹的收藏量
            redisOperator.del(RedisConf.REDIS_ARTICLE_COLLECT_COUNTS +
                    SysConf.SYMBOL_COLON + userId +
                    SysConf.SYMBOL_HYPHEN + favoritesId);
        }
        if (favoritesMapper.deleteByPrimaryKey(favoritesId) < 0) {
            log.error("delete favorites error");
            GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }


}
