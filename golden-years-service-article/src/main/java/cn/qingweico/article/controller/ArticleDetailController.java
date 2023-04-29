package cn.qingweico.article.controller;

import cn.qingweico.core.base.BaseController;
import cn.qingweico.article.service.ArticleDetailService;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.global.SysConst;
import cn.qingweico.global.RedisConst;
import cn.qingweico.pojo.Favorites;
import cn.qingweico.pojo.bo.CollectBO;
import cn.qingweico.pojo.bo.IdBO;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.pojo.vo.FavoritesVO;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.util.IpUtils;
import cn.qingweico.util.ServletReqUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zqw
 * @date 2022/4/15
 */
@Slf4j
@Api(value = "文章详情相关的接口定义", tags = {"文章详情相关的接口定义"})
@RequestMapping("/portal/article")
@RestController
public class ArticleDetailController extends BaseController {
    @Resource
    private ArticlePortalService articlePortalService;
    @Resource
    private ArticleDetailService articleDetailService;

    @Resource
    private ArticleController articleController;

    @GetMapping("detail")
    @ApiOperation(value = "文章详情", notes = "文章详情", httpMethod = "GET")
    public Result detail(@RequestParam String articleId) {
        ArticleDetailVO articleVO = articlePortalService.queryDetail(articleId);
        if (articleVO == null) {
            return Result.r(Response.ARTICLE_NOT_EXIST);
        }
        Map<String, Object> map = articleController.geAuthorInfo(articleVO.getAuthorId());
        articleVO.setAuthorName((String) map.get(SysConst.AUTHOR_NAME));
        articleVO.setAuthorFace((String) map.get(SysConst.AUTHOR_FACE));
        articleVO.setStarCounts(getCountsFromRedis(RedisConst.REDIS_ARTICLE_STAR_COUNTS + SysConst.SYMBOL_COLON + articleId));
        articleVO.setCollectCounts(getCountsFromRedis(RedisConst.REDIS_ARTICLE_COLLECT_COUNTS + SysConst.SYMBOL_COLON + articleId));
        articleVO.setCommentCounts(getCountsFromRedis(RedisConst.REDIS_ARTICLE_COMMENT_COUNTS + SysConst.SYMBOL_COLON + articleId));
        articleVO.setReadCounts(getCountsFromRedis(RedisConst.REDIS_ARTICLE_READ_COUNTS + SysConst.SYMBOL_COLON + articleId));
        return Result.ok(articleVO);

    }

    @PostMapping("incPagViews")
    @ApiOperation(value = "文章阅读量累加", notes = "文章阅读量累加", httpMethod = "POST")
    public Result incPagViews(@RequestParam String articleId) {
        HttpServletRequest request = ServletReqUtils.getRequest();
        String visitIp = IpUtils.getRequestIp(request);
        // 设置文章在一分钟内不可重复阅读(即不增加文章阅读量)
        redisCache.setNx60s(RedisConst.REDIS_ARTICLE_ALREADY_READ + SysConst.SYMBOL_COLON + articleId + SysConst.SYMBOL_COLON + visitIp, visitIp);
        redisCache.increment(RedisConst.REDIS_ARTICLE_READ_COUNTS + SysConst.SYMBOL_COLON + articleId, 1);
        return Result.ok();
    }

    @PostMapping("praise")
    @ApiOperation(value = "文章点赞", notes = "文章点赞", httpMethod = "POST")
    public Result praiseArticleById(@RequestBody IdBO idBO) {
        String articleId = idBO.getArticleId();
        String userId = idBO.getUserId();
        // 设置用户点赞标志位(已点赞)
        redisCache.set(RedisConst.REDIS_ARTICLE_ALREADY_STAR +
                SysConst.SYMBOL_COLON + articleId + SysConst.SYMBOL_HYPHEN + userId, "true");
        // 点赞数增加
        redisCache.increment(RedisConst.REDIS_ARTICLE_STAR_COUNTS + SysConst.SYMBOL_COLON + articleId, 1);
        return Result.r(Response.STAR_SUCCESS);
    }

    @PostMapping("unstar")
    @ApiOperation(value = "取消文章点赞", notes = "取消文章点赞", httpMethod = "POST")
    public Result cancelStar(@RequestBody IdBO idBO) {
        String articleId = idBO.getArticleId();
        String userId = idBO.getUserId();
        // 设置用户点赞标志位(未点赞)
        redisCache.del(RedisConst.REDIS_ARTICLE_ALREADY_STAR +
                SysConst.SYMBOL_COLON + articleId + SysConst.SYMBOL_HYPHEN + userId);
        // 点赞数减少
        redisCache.decrement(RedisConst.REDIS_ARTICLE_STAR_COUNTS + SysConst.SYMBOL_COLON + articleId, 1);
        return Result.r(Response.UN_STAR_SUCCESS);
    }


    @GetMapping("getArticleStarCounts")
    @ApiOperation(value = "获取文章点赞量", notes = "获取文章点赞量", httpMethod = "GET")
    public Result getArticleStarCounts(@RequestParam String articleId) {
        String articleStarCounts = redisCache.get(RedisConst.REDIS_ARTICLE_STAR_COUNTS + SysConst.SYMBOL_COLON + articleId);
        return Result.ok(articleStarCounts);
    }

    @PostMapping("isStar")
    @ApiOperation(value = "判断用户是否点赞了该文章", notes = "判断用户是否点赞了该文章", httpMethod = "POST")
    public Result isStar(@RequestBody IdBO idBO) {
        String articleId = idBO.getArticleId();
        String userId = idBO.getUserId();
        String isStar = redisCache.get(RedisConst.REDIS_ARTICLE_ALREADY_STAR +
                SysConst.SYMBOL_COLON + articleId + SysConst.SYMBOL_HYPHEN + userId);
        return Result.ok(StringUtils.isNotBlank(isStar));
    }

    @PostMapping("collect")
    @ApiOperation(value = "文章收藏", notes = "文章收藏", httpMethod = "POST")
    public Result collectArticleById(@RequestBody CollectBO collectBO) {
        String articleId = collectBO.getArticleId();
        String userId = collectBO.getUserId();
        String favoritesId = collectBO.getFavoritesId();
        if (StringUtils.isBlank(favoritesId)) {
            return Result.r(Response.SYSTEM_OPERATION_ERROR);
        }
        articleDetailService.collectArticle(collectBO);
        // 设置用户收藏标志位(已收藏)
        redisCache.set(RedisConst.REDIS_ARTICLE_ALREADY_COLLECT +
                SysConst.SYMBOL_COLON + articleId +
                SysConst.SYMBOL_HYPHEN + userId +
                SysConst.SYMBOL_HYPHEN + favoritesId, "true");
        // 文章收藏数增加
        redisCache.increment(RedisConst.REDIS_ARTICLE_COLLECT_COUNTS + SysConst.SYMBOL_COLON + articleId, 1);
        // 用户收藏夹收藏数增加
        redisCache.increment(RedisConst.REDIS_ARTICLE_COLLECT_COUNTS +
                SysConst.SYMBOL_COLON + userId +
                SysConst.SYMBOL_HYPHEN + favoritesId, 1);
        return Result.r(Response.COLLECT_SUCCESS);
    }

    @PostMapping("isCollect")
    @ApiOperation(value = "判断用户是否收藏了该文章", notes = "判断用户是否收藏了该文章", httpMethod = "POST")
    public Result isCollect(@RequestBody CollectBO collectBO) {
        String articleId = collectBO.getArticleId();
        String userId = collectBO.getUserId();
        // redis_article_already_collect:articleId-userId-*
        // 收藏夹中存在一个即为收藏;通配所有的favoritesId
        // 获取Redis中特定前缀
        Set<String> keys = redisCache.keys(RedisConst.REDIS_ARTICLE_ALREADY_COLLECT +
                SysConst.SYMBOL_COLON + articleId +
                SysConst.SYMBOL_HYPHEN + userId +
                SysConst.SYMBOL_HYPHEN + SysConst.SYMBOL_STAR);
        List<String> isCollect = redisCache.mGet(keys);
        return Result.ok(!isCollect.isEmpty());
    }

    @GetMapping("getArticleCollectCounts")
    @ApiOperation(value = "获取文章收藏量", notes = "获取文章收藏量", httpMethod = "GET")
    public Result getArticleCollectCounts(@RequestParam String articleId) {
        String articleCollectCounts = redisCache.get(RedisConst.REDIS_ARTICLE_COLLECT_COUNTS + SysConst.SYMBOL_COLON + articleId);
        return Result.ok(articleCollectCounts);
    }

    @PostMapping("uncollect")
    @ApiOperation(value = "取消文章收藏", notes = "取消文章收藏", httpMethod = "POST")
    public Result cancelCollect(@RequestBody CollectBO collectBO) {
        String articleId = collectBO.getArticleId();
        String userId = collectBO.getUserId();
        String favoritesId = collectBO.getFavoritesId();
        boolean cancelCollect = articleDetailService.cancelCollectArticle(collectBO);
        if (cancelCollect) {
            // 设置用户收藏标志位(未收藏)
            redisCache.del(RedisConst.REDIS_ARTICLE_ALREADY_COLLECT +
                    SysConst.SYMBOL_COLON + articleId +
                    SysConst.SYMBOL_HYPHEN + userId +
                    SysConst.SYMBOL_HYPHEN + favoritesId);
            // 文章收藏数减少
            redisCache.decrement(RedisConst.REDIS_ARTICLE_COLLECT_COUNTS + SysConst.SYMBOL_COLON + articleId, 1);
            // 用户收藏夹收藏数减少
            redisCache.decrement(RedisConst.REDIS_ARTICLE_COLLECT_COUNTS +
                    SysConst.SYMBOL_COLON + userId +
                    SysConst.SYMBOL_HYPHEN + favoritesId, 1);
            return Result.r(Response.UN_COLLECT_SUCCESS);
        } else {
            return Result.r(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @GetMapping("getFavorites")
    @ApiOperation(value = "获取我的收藏夹", notes = "获取我的收藏夹", httpMethod = "GET")
    public Result getFavorites(@RequestParam String userId, @RequestParam String articleId) {
        List<Favorites> favoriteList = articleDetailService.getFavoritesByUserId(userId);
        List<FavoritesVO> result = new ArrayList<>();
        for (Favorites favorite : favoriteList) {
            FavoritesVO favoritesVO = new FavoritesVO();
            BeanUtils.copyProperties(favorite, favoritesVO);
            String favoritesId = favorite.getId();
            // 精确匹配favoritesId
            String isFavoritesCollect = redisCache.get(RedisConst.REDIS_ARTICLE_ALREADY_COLLECT +
                    SysConst.SYMBOL_COLON + articleId +
                    SysConst.SYMBOL_HYPHEN + userId +
                    SysConst.SYMBOL_HYPHEN + favoritesId);
            // 判断用户的收藏夹是否收藏了该文章
            favoritesVO.setIsFavoritesCollect(StringUtils.isNotBlank(isFavoritesCollect));
            // 获取用户每个收藏夹收藏文章的数量
            String userFavoritesCollectCountsKey = RedisConst.REDIS_ARTICLE_COLLECT_COUNTS +
                    SysConst.SYMBOL_COLON + userId +
                    SysConst.SYMBOL_HYPHEN + favoritesId;
            favoritesVO.setFavoritesCollectCount(getCountsFromRedis(userFavoritesCollectCountsKey));
            result.add(favoritesVO);
        }
        return Result.ok(result);
    }

    @PostMapping("createFavorites")
    @ApiOperation(value = "创建收藏夹", notes = "创建收藏夹", httpMethod = "POST")
    public Result createFavorites(@RequestBody CollectBO collectBO) {
        articleDetailService.createFavorites(collectBO);
        return Result.r(Response.CREATE_FAVORITES_SUCCESS);
    }
}
