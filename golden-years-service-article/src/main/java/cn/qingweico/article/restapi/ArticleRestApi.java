package cn.qingweico.article.restapi;

import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.article.clients.UserBaseInfoClient;
import cn.qingweico.article.service.ArticleDetailService;
import cn.qingweico.article.service.ArticleService;
import cn.qingweico.enums.ArticleCoverType;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConf;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.Favorites;
import cn.qingweico.pojo.bo.CollectBO;
import cn.qingweico.pojo.bo.FavoritesBO;
import cn.qingweico.pojo.bo.NewArticleBO;
import cn.qingweico.pojo.vo.ArticleAdminVO;
import cn.qingweico.pojo.vo.FavoritesVO;
import cn.qingweico.pojo.vo.UserBasicInfoVO;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageInfo;
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
public class ArticleRestApi extends BaseRestApi {
    @Resource
    private ArticleService articleService;

    @Resource
    private ArticleDetailService articleDetailService;


    @ApiOperation(value = "发布新文章或者修改文章", notes = "发布新文章或者修改文章", httpMethod = "POST")
    @PostMapping("/user/publishOrUpdate")
    public GraceJsonResult createArticle(@RequestBody @Valid NewArticleBO newArticleBO) {
        String articleId = newArticleBO.getArticleId();
        // 添加文章
        if (StringUtils.isBlank(articleId)) {
            if (newArticleBO.getArticleType().equals(ArticleCoverType.ONE_IMAGE.type)) {
                if (StringUtils.isBlank(newArticleBO.getArticleCover())) {
                    return GraceJsonResult.errorCustom(ResponseStatusEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
                }
            } else if (newArticleBO.getArticleType().equals(ArticleCoverType.WORDS.type)) {
                newArticleBO.setArticleCover("");
            }

            // 判断分类Id是否存在 (从缓存中取出数据)
            String categoryJson = redisOperator.get(RedisConf.REDIS_ARTICLE_CATEGORY);
            List<Category> categories = JsonUtils.jsonToList(categoryJson, Category.class);
            if (categories == null) {
                return GraceJsonResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
            }
            Category res = categories.stream()
                    .filter(category -> category.getId().equals(newArticleBO.getCategoryId()))
                    .collect(toList()).get(0);

            if (res == null) {
                return GraceJsonResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
            }
            articleService.createArticle(newArticleBO);
            return new GraceJsonResult(ResponseStatusEnum.ARTICLE_PUBLISH_SUCCESS);
        } else {
            articleService.updateArticle(newArticleBO);
            return new GraceJsonResult(ResponseStatusEnum.ARTICLE_ALTER_SUCCESS);
        }

    }

    @ApiOperation(value = "条件查询用户所有的文章", notes = "条件查询用户所有的文章", httpMethod = "GET")
    @GetMapping("/user/query")
    public GraceJsonResult queryUserArticles(@RequestParam String userId,
                                             @RequestParam String keyword,
                                             @RequestParam String categoryId,
                                             @RequestParam Integer status,
                                             @RequestParam Date startDate,
                                             @RequestParam Date endDate,
                                             @RequestParam Integer page,
                                             @RequestParam Integer pageSize) {


        if (StringUtils.isBlank(userId)) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }
        checkPagingParams(page, pageSize);
        PagedGridResult res = articleService.queryUserArticles(userId,
                keyword,
                categoryId,
                status,
                startDate,
                endDate,
                page,
                pageSize);
        return GraceJsonResult.ok(res);
    }

    @ApiOperation(value = "管理员查询用户的所有文章列表", notes = "管理员查询用户的所有文章列表", httpMethod = "GET")
    @GetMapping("/admin/query")
    public GraceJsonResult query(@RequestParam String keyword,
                                 @RequestParam Integer status,
                                 @RequestParam String categoryId,
                                 @RequestParam String tagId,
                                 @RequestParam Integer deleteStatus,
                                 @RequestParam Date startDateStr,
                                 @RequestParam Date endDateStr,
                                 @RequestParam Integer page,
                                 @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedGridResult result = articleService.query(keyword,
                status,
                categoryId,
                tagId,
                deleteStatus,
                startDateStr,
                endDateStr,
                page,
                pageSize);
        String toJson = JsonUtils.objectToJson(result.getRows());
        List<ArticleAdminVO> articles = JsonUtils.jsonToList(toJson, ArticleAdminVO.class);
        for (ArticleAdminVO articleAdminVO : articles) {
            Map<String, Object> authorInfo = geAuthorInfo(articleAdminVO.getAuthorId());
            articleAdminVO.setAuthorName((String) authorInfo.get("authorName"));
            articleAdminVO.setAuthorFace((String) authorInfo.get("authorFace"));
            articleAdminVO.setFansCounts((Integer) authorInfo.get("fansCounts"));
            articleAdminVO.setFollowCounts((Integer) authorInfo.get("followCounts"));
        }
        return GraceJsonResult.ok(result);
    }

    @ApiOperation(value = "管理员对文章进行审核", notes = "管理员对文章进行审核", httpMethod = "POST")
    @PostMapping("/admin/doReview")
    public GraceJsonResult doReview(String articleId, Integer passOrNot) {
        Integer pendingStatus;
        // 文章审核通过
        if (YesOrNo.YES.type.equals(passOrNot)) {
            pendingStatus = ArticleReviewStatus.SUCCESS.type;
        }
        // 文章审核不通过
        else if (YesOrNo.NO.type.equals(passOrNot)) {
            pendingStatus = ArticleReviewStatus.FAILED.type;
        } else {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
        articleService.updateArticleStatus(articleId, pendingStatus);
        if (YesOrNo.YES.type.equals(passOrNot)) {
            return new GraceJsonResult(ResponseStatusEnum.ARTICLE_REVIEW_PASS);
        } else {
            return new GraceJsonResult(ResponseStatusEnum.ARTICLE_REVIEW_FAIL);
        }
    }

    @ApiOperation(value = "重新对文章进行审核", notes = "重新对文章进行审核", httpMethod = "POST")
    @PostMapping("/admin/reReview")
    public GraceJsonResult reReview(String articleId) {
        articleService.reReview(articleId);
        return new GraceJsonResult(ResponseStatusEnum.ARTICLE_RE_REVIEW_PASS);
    }

    @ApiOperation(value = "管理员对文章进行删除(delete sql)", notes = "管理员对文章进行删除(delete sql)", httpMethod = "POST")
    @PostMapping("/admin/delete")
    public GraceJsonResult delete(String articleId) {
        articleService.deleteArticle(articleId);
        return new GraceJsonResult(ResponseStatusEnum.DELETE_SUCCESS);
    }

    @ApiOperation(value = "管理员撤回用户对文章的删除", notes = "管理员撤回用户对文章的删除", httpMethod = "POST")
    @PostMapping("/admin/withdrawDelete")
    public GraceJsonResult withdrawDelete(String articleId) {
        articleService.withdrawDelete(articleId);
        return new GraceJsonResult(ResponseStatusEnum.DELETE_HAS_WITHDRAW);
    }

    @ApiOperation(value = "用户删除文章", notes = "用户删除文章", httpMethod = "POST")
    @PostMapping("/user/delete")
    public GraceJsonResult delete(String userId, String articleId) {
        articleService.deleteArticle(userId, articleId);
        return new GraceJsonResult(ResponseStatusEnum.ARTICLE_DELETE_SUCCESS);

    }

    @ApiOperation(value = "用户撤回文章", notes = "用户撤回文章", httpMethod = "POST")
    @PostMapping("/user/withdraw")
    public GraceJsonResult withdraw(String userId, String articleId) {
        articleService.withdrawArticle(userId, articleId);
        return new GraceJsonResult(ResponseStatusEnum.ARTICLE_WITHDRAW_SUCCESS);
    }

    @GetMapping("getFavorites")
    @ApiOperation(value = "获取我的收藏夹", notes = "获取我的收藏夹", httpMethod = "GET")
    public GraceJsonResult getFavorites(@RequestParam String userId) {
        List<Favorites> favoriteList = articleDetailService.getFavoritesByUserId(userId);
        List<FavoritesVO> result = new ArrayList<>();
        for (Favorites favorite : favoriteList) {
            FavoritesVO favoritesVO = new FavoritesVO();
            BeanUtils.copyProperties(favorite, favoritesVO);
            String favoritesId = favorite.getId();
            // 获取用户每个收藏夹收藏文章的数量
            String userFavoritesCollectCountsKey = RedisConf.REDIS_ARTICLE_COLLECT_COUNTS +
                    SysConf.SYMBOL_COLON + userId +
                    SysConf.SYMBOL_HYPHEN + favoritesId;
            favoritesVO.setFavoritesCollectCount(getCountsFromRedis(userFavoritesCollectCountsKey));
            result.add(favoritesVO);
        }
        return GraceJsonResult.ok(result);
    }

    @PostMapping("alterFavorites")
    @ApiOperation(value = "修改收藏夹信息", notes = "修改收藏夹信息", httpMethod = "POST")
    public GraceJsonResult alterFavorites(@RequestBody @Valid FavoritesBO favoritesBO) {
        articleDetailService.editFavorites(favoritesBO);
        return new GraceJsonResult(ResponseStatusEnum.ALERT_SUCCESS);
    }

    @PostMapping("deleteFavorites")
    @ApiOperation(value = "删除收藏夹", notes = "删除收藏夹", httpMethod = "POST")
    public GraceJsonResult deleteFavorites(@RequestBody @Valid CollectBO collectBO) {
        String favorites = collectBO.getFavoritesId();
        if (StringUtils.isBlank(favorites)) {
            return GraceJsonResult.error();
        }
        articleDetailService.deleteFavorites(collectBO);
        return new GraceJsonResult(ResponseStatusEnum.DELETE_SUCCESS);
    }

    @Resource
    private UserBaseInfoClient client;

    public UserBasicInfoVO getUserBasicInfoClient(String id) {
        UserBasicInfoVO userBasicInfo;
        GraceJsonResult result = client.getUserBasicInfo(id);
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
            map.put("authorName", userBasicInfo.getNickname());
            map.put("authorFace", userBasicInfo.getFace());
            map.put("followCounts", userBasicInfo.getMyFollowCounts());
            map.put("fansCounts", userBasicInfo.getMyFansCounts());
        } else {
            GraceException.error(ResponseStatusEnum.SYSTEM_ERROR);
            log.error("用户服务不可用");
        }
        return map;
    }
}
