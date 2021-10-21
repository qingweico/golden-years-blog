package cn.qingweico.api.controller.article;

import cn.qingweico.grace.result.GraceJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/12
 */
@Api(value = "主页文章相关的接口定义", tags = {"主页文章相关的接口定义"})
@RequestMapping("/portal/article")
public interface ArticlePortalControllerApi {

    @ApiOperation(value = "首页查询文章列表(elasticSearch)", notes = "首页查询文章列表", httpMethod = "GET")
    @GetMapping("es/list")
    GraceJsonResult queryAllViaEs(@RequestParam String keyword,
                                  @RequestParam Integer category,
                                  @RequestParam Integer page,
                                  @RequestParam Integer pageSize);


    @ApiOperation(value = "首页查询文章列表", notes = "首页查询文章列表", httpMethod = "GET")
    @GetMapping("list")
    GraceJsonResult queryAll(@RequestParam String keyword,
                             @RequestParam Integer category,
                             @RequestParam Integer page,
                             @RequestParam Integer pageSize);

    @ApiOperation(value = "首页查询分类列表", notes = "首页查询分类列表", httpMethod = "GET")
    @GetMapping("/getCategoryList")
    GraceJsonResult getCategoryList();


    @ApiOperation(value = "首页查询热文列表", notes = "首页查询热文列表", httpMethod = "GET")
    @GetMapping("hotList")
    GraceJsonResult hotList();

    @GetMapping("queryArticleListOfWriter")
    @ApiOperation(value = "查询作家发布的所有文章列表", notes = "查询作家发布的所有文章列表", httpMethod = "GET")
    GraceJsonResult queryArticleListOfWriter(@RequestParam String writerId,
                                             @RequestParam Integer page,
                                             @RequestParam Integer pageSize);

    @GetMapping("queryGoodArticleListOfWriter")
    @ApiOperation(value = "作家页面查询近期佳文", notes = "作家页面查询近期佳文", httpMethod = "GET")
    GraceJsonResult queryGoodArticleListOfWriter(@RequestParam String writerId);

    @GetMapping("detail")
    @ApiOperation(value = "文章详情", notes = "文章详情", httpMethod = "GET")
    GraceJsonResult detail(@RequestParam String articleId);

    @PostMapping("readArticle")
    @ApiOperation(value = "文章阅读量累加", notes = "文章阅读量累加", httpMethod = "POST")
    GraceJsonResult readArticle(@RequestParam String articleId,
                                HttpServletRequest req);

    @GetMapping("readCounts")
    @ApiOperation(value = "文章阅读数", notes = "文章阅读数", httpMethod = "GET")
    Integer readCounts(@RequestParam String articleId,
                       HttpServletRequest req);
}
