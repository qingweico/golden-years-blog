package cn.qingweico.api.controller.article;

import cn.qingweico.result.GraceJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zqw
 * @date 2021/9/12
 */
@Api(value = "主页文章相关的接口定义", tags = {"主页文章相关的接口定义"})
@RequestMapping("/portal/article")
public interface ArticlePortalControllerApi {

    /**
     * 首页查询文章列表(elasticSearch)
     *
     * @param keyword  关键字
     * @param category 文章类别
     * @param page     起始分页
     * @param pageSize 每页数量
     * @return GraceJsonResult
     */
    @ApiOperation(value = "首页查询文章列表(elasticSearch)", notes = "首页查询文章列表(elasticSearch)", httpMethod = "GET")
    @GetMapping("es/list")
    GraceJsonResult queryAllViaEs(@RequestParam String keyword,
                                  @RequestParam Integer category,
                                  @RequestParam Integer page,
                                  @RequestParam Integer pageSize);


    /**
     * 首页查询文章列表(sql)
     *
     * @param keyword  关键字
     * @param category 文章类别
     * @param page     起始分页
     * @param pageSize 每页数量
     * @return GraceJsonResult
     */
    @ApiOperation(value = "首页查询文章列表(sql)", notes = "首页查询文章列表(sql)", httpMethod = "GET")
    @GetMapping("list")
    GraceJsonResult query(@RequestParam String keyword,
                          @RequestParam Integer category,
                          @RequestParam Integer page,
                          @RequestParam Integer pageSize);

    /**
     * 首页查询分类列表
     *
     * @return GraceJsonResult
     */
    @ApiOperation(value = "首页查询分类列表", notes = "首页查询分类列表", httpMethod = "GET")
    @GetMapping("category")
    GraceJsonResult getCategoryList();


    /**
     * 首页文章排行
     *
     * @return GraceJsonResult
     */
    @ApiOperation(value = "首页文章排行", notes = "首页文章排行", httpMethod = "GET")
    @GetMapping("/rank")
    GraceJsonResult hotList();

    /**
     * (个人中心)查询作家发布的所有文章列表
     *
     * @param authorId 作者id
     * @param page     起始分页
     * @param pageSize 每页数量
     * @return GraceJsonResult
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询作家发布的所有文章列表", notes = "查询作家发布的所有文章列表", httpMethod = "GET")
    GraceJsonResult queryArticleListByAuthorId(@RequestParam @PathVariable("id") String authorId,
                                               @RequestParam Integer page,
                                               @RequestParam Integer pageSize);

    /**
     * 查询个人中心热门文章
     *
     * @param authorId 作者id
     * @return GraceJsonResult
     */
    @GetMapping("rank/{id}")
    @ApiOperation(value = "查询个人中心热门文章", notes = "查询个人中心热门文章", httpMethod = "GET")
    GraceJsonResult queryGoodArticleListOfAuthor(@RequestParam @PathVariable("id") String authorId);

    /**
     * 文章详情
     *
     * @param articleId 作者id
     * @return GraceJsonResult
     */
    @GetMapping("detail")
    @ApiOperation(value = "文章详情", notes = "文章详情", httpMethod = "GET")
    GraceJsonResult detail(@RequestParam String articleId);

    /**
     * 文章阅读量累加
     *
     * @param articleId 作者id
     * @param req       HttpServletRequest
     * @return GraceJsonResult
     */
    @PostMapping("readArticle")
    @ApiOperation(value = "文章阅读量累加", notes = "文章阅读量累加", httpMethod = "POST")
    GraceJsonResult readArticle(@RequestParam String articleId,
                                HttpServletRequest req);

    /**
     * 文章阅读数
     *
     * @param articleId 作者id
     * @param req       HttpServletRequest
     * @return Integer
     */
    @GetMapping("readCounts")
    @ApiOperation(value = "文章阅读数", notes = "文章阅读数", httpMethod = "GET")
    Integer readCounts(@RequestParam String articleId,
                       HttpServletRequest req);
}
