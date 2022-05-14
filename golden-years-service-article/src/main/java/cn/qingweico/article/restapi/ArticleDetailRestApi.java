package cn.qingweico.article.restapi;

import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.article.service.ArticleDetailService;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.global.SysConf;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.Favorites;
import cn.qingweico.pojo.bo.CollectBO;
import cn.qingweico.pojo.bo.IdBO;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.pojo.vo.FavoritesVO;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
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
public class ArticleDetailRestApi extends BaseRestApi {
    @Resource
    private ArticlePortalService articlePortalService;
    @Resource
    private ArticleDetailService articleDetailService;

    @Resource
    private ArticleRestApi articleRestApi;

    @GetMapping("detail")
    @ApiOperation(value = "文章详情", notes = "文章详情", httpMethod = "GET")
    public GraceJsonResult detail(@RequestParam String articleId) {
        ArticleDetailVO articleVO = articlePortalService.queryDetail(articleId);
        if (articleVO == null) {
            return new GraceJsonResult(ResponseStatusEnum.ARTICLE_NOT_EXIST);
        }
        Map<String, Object> map = articleRestApi.geAuthorInfo(articleVO.getAuthorId());
        articleVO.setAuthorName((String) map.get(SysConf.AUTHOR_NAME));
        articleVO.setAuthorFace((String) map.get(SysConf.AUTHOR_FACE));
        articleVO.setStarCounts(getCountsFromRedis(RedisConf.REDIS_ARTICLE_STAR_COUNTS + SysConf.SYMBOL_COLON + articleId));
        articleVO.setCollectCounts(getCountsFromRedis(RedisConf.REDIS_ARTICLE_COLLECT_COUNTS + SysConf.SYMBOL_COLON + articleId));
        articleVO.setCommentCounts(getCountsFromRedis(RedisConf.REDIS_ARTICLE_COMMENT_COUNTS + SysConf.SYMBOL_COLON + articleId));
        articleVO.setReadCounts(getCountsFromRedis(RedisConf.REDIS_ARTICLE_READ_COUNTS + SysConf.SYMBOL_COLON + articleId));
        return GraceJsonResult.ok(articleVO);

    }

    @PostMapping("incPagViews")
    @ApiOperation(value = "文章阅读量累加", notes = "文章阅读量累加", httpMethod = "POST")
    public GraceJsonResult incPagViews(@RequestParam String articleId) {
        HttpServletRequest request = ServletReqUtils.getRequest();
        String visitIp = IpUtils.getRequestIp(request);
        // 设置文章在一分钟内不可重复阅读(即不增加文章阅读量)
        redisOperator.setnx60s(RedisConf.REDIS_ARTICLE_ALREADY_READ + SysConf.SYMBOL_COLON + articleId + SysConf.SYMBOL_COLON + visitIp, visitIp);
        redisOperator.increment(RedisConf.REDIS_ARTICLE_READ_COUNTS + SysConf.SYMBOL_COLON + articleId, 1);
        return GraceJsonResult.ok();
    }

    @PostMapping("praise")
    @ApiOperation(value = "文章点赞", notes = "文章点赞", httpMethod = "POST")
    public GraceJsonResult praiseArticleById(@RequestBody IdBO idBO) {
        String articleId = idBO.getArticleId();
        String userId = idBO.getUserId();
        // 设置用户点赞标志位(已点赞)
        redisOperator.set(RedisConf.REDIS_ARTICLE_ALREADY_STAR +
                SysConf.SYMBOL_COLON + articleId + SysConf.SYMBOL_HYPHEN + userId, "true");
        // 点赞数增加
        redisOperator.increment(RedisConf.REDIS_ARTICLE_STAR_COUNTS + SysConf.SYMBOL_COLON + articleId, 1);
        return new GraceJsonResult(ResponseStatusEnum.STAR_SUCCESS);
    }

    @PostMapping("unstar")
    @ApiOperation(value = "取消文章点赞", notes = "取消文章点赞", httpMethod = "POST")
    public GraceJsonResult cancelStar(@RequestBody IdBO idBO) {
        String articleId = idBO.getArticleId();
        String userId = idBO.getUserId();
        // 设置用户点赞标志位(未点赞)
        redisOperator.del(RedisConf.REDIS_ARTICLE_ALREADY_STAR +
                SysConf.SYMBOL_COLON + articleId + SysConf.SYMBOL_HYPHEN + userId);
        // 点赞数减少
        redisOperator.decrement(RedisConf.REDIS_ARTICLE_STAR_COUNTS + SysConf.SYMBOL_COLON + articleId, 1);
        return new GraceJsonResult(ResponseStatusEnum.UN_STAR_SUCCESS);
    }


    @GetMapping("getArticleStarCounts")
    @ApiOperation(value = "获取文章点赞量", notes = "获取文章点赞量", httpMethod = "GET")
    public GraceJsonResult getArticleStarCounts(@RequestParam String articleId) {
        String articleStarCounts = redisOperator.get(RedisConf.REDIS_ARTICLE_STAR_COUNTS + SysConf.SYMBOL_COLON + articleId);
        return GraceJsonResult.ok(articleStarCounts);
    }

    @PostMapping("isStar")
    @ApiOperation(value = "判断用户是否点赞了该文章", notes = "判断用户是否点赞了该文章", httpMethod = "POST")
    public GraceJsonResult isStar(@RequestBody IdBO idBO) {
        String articleId = idBO.getArticleId();
        String userId = idBO.getUserId();
        String isStar = redisOperator.get(RedisConf.REDIS_ARTICLE_ALREADY_STAR +
                SysConf.SYMBOL_COLON + articleId + SysConf.SYMBOL_HYPHEN + userId);
        return GraceJsonResult.ok(StringUtils.isNotBlank(isStar));
    }

    @PostMapping("collect")
    @ApiOperation(value = "文章收藏", notes = "文章收藏", httpMethod = "POST")
    public GraceJsonResult collectArticleById(@RequestBody CollectBO collectBO) {
        String articleId = collectBO.getArticleId();
        String userId = collectBO.getUserId();
        String favoritesId = collectBO.getFavoritesId();
        if (StringUtils.isBlank(favoritesId)) {
            return new GraceJsonResult(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        articleDetailService.collectArticle(collectBO);
        // 设置用户收藏标志位(已收藏)
        redisOperator.set(RedisConf.REDIS_ARTICLE_ALREADY_COLLECT +
                SysConf.SYMBOL_COLON + articleId +
                SysConf.SYMBOL_HYPHEN + userId +
                SysConf.SYMBOL_HYPHEN + favoritesId, "true");
        // 文章收藏数增加
        redisOperator.increment(RedisConf.REDIS_ARTICLE_COLLECT_COUNTS + SysConf.SYMBOL_COLON + articleId, 1);
        // 用户收藏夹收藏数增加
        redisOperator.increment(RedisConf.REDIS_ARTICLE_COLLECT_COUNTS +
                SysConf.SYMBOL_COLON + userId +
                SysConf.SYMBOL_HYPHEN + favoritesId, 1);
        return new GraceJsonResult(ResponseStatusEnum.COLLECT_SUCCESS);
    }

    @PostMapping("isCollect")
    @ApiOperation(value = "判断用户是否收藏了该文章", notes = "判断用户是否收藏了该文章", httpMethod = "POST")
    public GraceJsonResult isCollect(@RequestBody CollectBO collectBO) {
        String articleId = collectBO.getArticleId();
        String userId = collectBO.getUserId();
        // redis_article_already_collect:articleId-userId-*
        // 收藏夹中存在一个即为收藏;通配所有的favoritesId
        // 获取Redis中特定前缀
        Set<String> keys = redisOperator.keys(RedisConf.REDIS_ARTICLE_ALREADY_COLLECT +
                SysConf.SYMBOL_COLON + articleId +
                SysConf.SYMBOL_HYPHEN + userId +
                SysConf.SYMBOL_HYPHEN + SysConf.SYMBOL_STAR);
        List<String> isCollect = redisOperator.mget(keys);
        return GraceJsonResult.ok(!isCollect.isEmpty());
    }

    @GetMapping("getArticleCollectCounts")
    @ApiOperation(value = "获取文章收藏量", notes = "获取文章收藏量", httpMethod = "GET")
    public GraceJsonResult getArticleCollectCounts(@RequestParam String articleId) {
        String articleCollectCounts = redisOperator.get(RedisConf.REDIS_ARTICLE_COLLECT_COUNTS + SysConf.SYMBOL_COLON + articleId);
        return GraceJsonResult.ok(articleCollectCounts);
    }

    @PostMapping("uncollect")
    @ApiOperation(value = "取消文章收藏", notes = "取消文章收藏", httpMethod = "POST")
    public GraceJsonResult cancelCollect(@RequestBody CollectBO collectBO) {
        String articleId = collectBO.getArticleId();
        String userId = collectBO.getUserId();
        String favoritesId = collectBO.getFavoritesId();
        boolean cancelCollect = articleDetailService.cancelCollectArticle(collectBO);
        if (cancelCollect) {
            // 设置用户收藏标志位(未收藏)
            redisOperator.del(RedisConf.REDIS_ARTICLE_ALREADY_COLLECT +
                    SysConf.SYMBOL_COLON + articleId +
                    SysConf.SYMBOL_HYPHEN + userId +
                    SysConf.SYMBOL_HYPHEN + favoritesId);
            // 文章收藏数减少
            redisOperator.decrement(RedisConf.REDIS_ARTICLE_COLLECT_COUNTS + SysConf.SYMBOL_COLON + articleId, 1);
            // 用户收藏夹收藏数减少
            redisOperator.decrement(RedisConf.REDIS_ARTICLE_COLLECT_COUNTS +
                    SysConf.SYMBOL_COLON + userId +
                    SysConf.SYMBOL_HYPHEN + favoritesId, 1);
            return new GraceJsonResult(ResponseStatusEnum.UN_COLLECT_SUCCESS);
        } else {
            return new GraceJsonResult(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    @GetMapping("getFavorites")
    @ApiOperation(value = "获取我的收藏夹", notes = "获取我的收藏夹", httpMethod = "GET")
    public GraceJsonResult getFavorites(@RequestParam String userId, @RequestParam String articleId) {
        List<Favorites> favoriteList = articleDetailService.getFavoritesByUserId(userId);
        List<FavoritesVO> result = new ArrayList<>();
        for (Favorites favorite : favoriteList) {
            FavoritesVO favoritesVO = new FavoritesVO();
            BeanUtils.copyProperties(favorite, favoritesVO);
            String favoritesId = favorite.getId();
            // 精确匹配favoritesId
            String isFavoritesCollect = redisOperator.get(RedisConf.REDIS_ARTICLE_ALREADY_COLLECT +
                    SysConf.SYMBOL_COLON + articleId +
                    SysConf.SYMBOL_HYPHEN + userId +
                    SysConf.SYMBOL_HYPHEN + favoritesId);
            // 判断用户的收藏夹是否收藏了该文章
            favoritesVO.setIsFavoritesCollect(StringUtils.isNotBlank(isFavoritesCollect));
            // 获取用户每个收藏夹收藏文章的数量
            String userFavoritesCollectCountsKey = RedisConf.REDIS_ARTICLE_COLLECT_COUNTS +
                    SysConf.SYMBOL_COLON + userId +
                    SysConf.SYMBOL_HYPHEN + favoritesId;
            favoritesVO.setFavoritesCollectCount(getCountsFromRedis(userFavoritesCollectCountsKey));
            result.add(favoritesVO);
        }
        return GraceJsonResult.ok(result);
    }

    @PostMapping("createFavorites")
    @ApiOperation(value = "创建收藏夹", notes = "创建收藏夹", httpMethod = "POST")
    public GraceJsonResult createFavorites(@RequestBody CollectBO collectBO) {
        articleDetailService.createFavorites(collectBO);
        return new GraceJsonResult(ResponseStatusEnum.CREATE_FAVORITES_SUCCESS);
    }
}
