package cn.qingweico.article.controller;

import cn.qingweico.core.base.BaseController;
import cn.qingweico.article.clients.UserBaseInfoClient;
import cn.qingweico.article.service.ArticleDetailService;
import cn.qingweico.article.service.ArticleService;
import cn.qingweico.entity.Article;
import cn.qingweico.entity.Category;
import cn.qingweico.entity.Favorites;
import cn.qingweico.entity.model.NewArticleBO;
import cn.qingweico.entity.model.UserBasicInfoVO;
import cn.qingweico.enums.ArticleCoverType;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConst;
import cn.qingweico.global.RedisConst;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author zqw
 * @date 2021/9/11
 */
@Slf4j
@RestController
@Api(value = "文章操作相关的接口定义", tags = {"文章操作相关的接口定义"})
@RequestMapping("article")
public class ArticleController extends BaseController {
    @Resource
    private ArticleService articleService;

    @Resource
    private ArticleDetailService articleDetailService;


    @ApiOperation(value = "发布新文章或者修改文章", notes = "发布新文章或者修改文章", httpMethod = "POST")
    @PostMapping("/user/publishOrUpdate")
    public Result createArticle(@RequestBody @Valid NewArticleBO newArticleBO) {
        String articleId = newArticleBO.getArticleId();
        // 添加文章
        if (StringUtils.isBlank(articleId)) {
            if (newArticleBO.getArticleType().equals(ArticleCoverType.ONE_IMAGE.getVal())) {
                if (StringUtils.isBlank(newArticleBO.getArticleCover())) {
                    return Result.r(Response.ARTICLE_COVER_NOT_EXIST_ERROR);
                }
            } else if (newArticleBO.getArticleType().equals(ArticleCoverType.WORDS.getVal())) {
                newArticleBO.setArticleCover(SysConst.EMPTY_STRING);
            }

            // 判断分类Id是否存在 (从缓存中取出数据)
            String categoryJson = redisCache.get(RedisConst.REDIS_ARTICLE_CATEGORY);
            List<Category> categories = JsonUtils.jsonToList(categoryJson, Category.class);
            if (categories == null) {
                return Result.r(Response.SYSTEM_ERROR);
            }
            Category category = categories.stream()
                    .filter(e -> e.getId().equals(newArticleBO.getCategoryId()))
                    .collect(toList()).get(0);

            if (category == null) {
                return Result.r(Response.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
            }
            articleService.createArticle(newArticleBO);
            return Result.r(Response.ARTICLE_PUBLISH_SUCCESS);
        } else {
            articleService.updateArticle(newArticleBO);
            return Result.r(Response.ARTICLE_ALTER_SUCCESS);
        }

    }

    @ApiOperation(value = "条件查询用户所有的文章", notes = "条件查询用户所有的文章", httpMethod = "GET")
    @GetMapping("/user/query")
    public Result queryUserArticles(@RequestParam String userId,
                                    @RequestParam String keyword,
                                    @RequestParam String categoryId,
                                    @RequestParam Integer status,
                                    @RequestParam Date startDate,
                                    @RequestParam Date endDate,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize) {


        if (StringUtils.isBlank(userId)) {
            return Result.r(Response.ARTICLE_QUERY_PARAMS_ERROR);
        }
        checkPagingParams(page, pageSize);
        PagedResult res = articleService.queryUserArticles(userId,
                keyword,
                categoryId,
                status,
                startDate,
                endDate,
                page,
                pageSize);
        return Result.ok(res);
    }

    @ApiOperation(value = "后台查询用户的所有文章列表", notes = "后台查询用户的所有文章列表", httpMethod = "GET")
    @GetMapping("/admin/query")
    public Result query(@RequestParam String keyword,
                        @RequestParam Integer status,
                        @RequestParam String categoryId,
                        @RequestParam String tagId,
                        @RequestParam Integer deleteStatus,
                        @RequestParam Date startDateStr,
                        @RequestParam Date endDateStr,
                        @RequestParam Integer page,
                        @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedResult result = articleService.query(keyword,
                status,
                categoryId,
                tagId,
                deleteStatus,
                startDateStr,
                endDateStr,
                page,
                pageSize);
        String toJson = JsonUtils.objectToJson(result.getRows());
        List<Article> articles = JsonUtils.jsonToList(toJson, Article.class);
        result.setRows(articles);
        return Result.ok(result);
    }

    @ApiOperation(value = "管理员对文章进行审核", notes = "管理员对文章进行审核", httpMethod = "POST")
    @PostMapping("/admin/doReview")
    public Result doReview(String articleId, Integer passOrNot) {
        Integer pendingStatus;
        // 文章审核通过
        if (YesOrNo.YES.type.equals(passOrNot)) {
            pendingStatus = ArticleReviewStatus.SUCCESS.type;
        }
        // 文章审核不通过
        else if (YesOrNo.NO.type.equals(passOrNot)) {
            pendingStatus = ArticleReviewStatus.FAILED.type;
        } else {
            return Result.r(Response.ARTICLE_REVIEW_ERROR);
        }
        articleService.updateArticleStatus(articleId, pendingStatus);
        if (YesOrNo.YES.type.equals(passOrNot)) {
            return Result.r(Response.ARTICLE_REVIEW_PASS);
        } else {
            return Result.r(Response.ARTICLE_REVIEW_FAIL);
        }
    }

    @ApiOperation(value = "重新对文章进行审核", notes = "重新对文章进行审核", httpMethod = "POST")
    @PostMapping("/admin/reReview")
    public Result reReview(String articleId) {
        articleService.reReview(articleId);
        return Result.r(Response.ARTICLE_RE_REVIEW_PASS);
    }

    @ApiOperation(value = "管理员对文章进行删除(delete sql)", notes = "管理员对文章进行删除(delete sql)", httpMethod = "POST")
    @PostMapping("/admin/delete")
    public Result delete(String articleId) {
        articleService.deleteArticle(articleId);
        return Result.r(Response.DELETE_SUCCESS);
    }

    @ApiOperation(value = "管理员撤回用户对文章的删除", notes = "管理员撤回用户对文章的删除", httpMethod = "POST")
    @PostMapping("/admin/withdrawDelete")
    public Result withdrawDelete(String articleId) {
        articleService.withdrawDelete(articleId);
        return Result.r(Response.DELETE_HAS_WITHDRAW);
    }

    @ApiOperation(value = "用户删除文章", notes = "用户删除文章", httpMethod = "POST")
    @PostMapping("/user/delete")
    public Result delete(String userId, String articleId) {
        articleService.deleteArticle(userId, articleId);
        return Result.r(Response.ARTICLE_DELETE_SUCCESS);

    }

    @ApiOperation(value = "用户撤回文章", notes = "用户撤回文章", httpMethod = "POST")
    @PostMapping("/user/withdraw")
    public Result withdraw(String userId, String articleId) {
        articleService.withdrawArticle(userId, articleId);
        return Result.r(Response.ARTICLE_WITHDRAW_SUCCESS);
    }

    @GetMapping("getFavorites")
    @ApiOperation(value = "获取我的收藏夹", notes = "获取我的收藏夹", httpMethod = "GET")
    public Result getFavorites(@RequestParam String userId) {
        List<Favorites> favoriteList = articleDetailService.getFavoritesByUserId(userId);
        List<FavoritesVO> result = new ArrayList<>();
        for (Favorites favorite : favoriteList) {
            FavoritesVO favoritesVO = new FavoritesVO();
            BeanUtils.copyProperties(favorite, favoritesVO);
            String favoritesId = favorite.getId();
            // 获取用户每个收藏夹收藏文章的数量
            String userFavoritesCollectCountsKey = RedisConst.REDIS_ARTICLE_COLLECT_COUNTS +
                    SysConst.SYMBOL_COLON + userId +
                    SysConst.SYMBOL_HYPHEN + favoritesId;
            favoritesVO.setFavoritesCollectCount(getCountsFromRedis(userFavoritesCollectCountsKey));
            result.add(favoritesVO);
        }
        return Result.ok(result);
    }

    @PostMapping("alterFavorites")
    @ApiOperation(value = "修改收藏夹信息", notes = "修改收藏夹信息", httpMethod = "POST")
    public Result alterFavorites(@RequestBody @Valid FavoritesBO favoritesBO) {
        articleDetailService.editFavorites(favoritesBO);
        return Result.r(Response.ALERT_SUCCESS);
    }

    @PostMapping("deleteFavorites")
    @ApiOperation(value = "删除收藏夹", notes = "删除收藏夹", httpMethod = "POST")
    public Result deleteFavorites(@RequestBody @Valid CollectBO collectBO) {
        String favorites = collectBO.getFavoritesId();
        if (StringUtils.isBlank(favorites)) {
            return Result.error();
        }
        articleDetailService.deleteFavorites(collectBO);
        return Result.r(Response.DELETE_SUCCESS);
    }

    @Resource
    private UserBaseInfoClient client;

    public UserBasicInfoVO getUserBasicInfoClient(String id) {
        UserBasicInfoVO userBasicInfo;
        Result result = client.getUserBasicInfo(id);
        if (result.getData() != null) {
            String userJson = JsonUtils.objectToJson(result.getData());
            userBasicInfo = JsonUtils.jsonToPojo(userJson, UserBasicInfoVO.class);
        } else {
            userBasicInfo = new UserBasicInfoVO();
        }
        return userBasicInfo;
    }


    public Map<String, Object> geAuthorInfo(String authorId) {
        Map<String, Object> map = new HashMap<>(4);
        UserBasicInfoVO userBasicInfo = getUserBasicInfoClient(authorId);
        if (StringUtils.isNotBlank(userBasicInfo.getId())) {
            map.put(SysConst.AUTHOR_NAME, userBasicInfo.getNickname());
            map.put(SysConst.AUTHOR_FACE, userBasicInfo.getFace());
            map.put(SysConst.FOLLOW_COUNTS, userBasicInfo.getMyFollowCounts());
            map.put(SysConst.FANS_COUNTS, userBasicInfo.getMyFansCounts());
        } else {
            GraceException.error(Response.SYSTEM_ERROR);
            log.error("用户服务不可用");
        }
        return map;
    }
}
