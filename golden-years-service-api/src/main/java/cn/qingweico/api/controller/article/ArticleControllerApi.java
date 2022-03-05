package cn.qingweico.api.controller.article;

import cn.qingweico.enums.YesOrNo;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.pojo.bo.NewArticleBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/11
 */
@Api(value = "文章发布相关的接口定义", tags = {"文章发布相关的接口定义"})
@RequestMapping("article")
public interface ArticleControllerApi {


    /**
     * 用户发布新文章
     *
     * @param newArticleBO NewArticleBO
     * @return GraceJsonResult
     */
    @ApiOperation(value = "发布新文章", notes = "发布新文章", httpMethod = "POST")
    @PostMapping("/user/publish")
    GraceJsonResult createArticle(@RequestBody @Valid NewArticleBO newArticleBO);

    /**
     * 条件查询用户所有的文章
     *
     * @param userId     用户id
     * @param keyword    关键字(文章标题)
     * @param categoryId 文章类别
     * @param status     文章状态
     * @param startDate  起始时间
     * @param endDate    截至时间
     * @param page       分页开始页数
     * @param pageSize   每页数量
     * @return GraceJsonResult
     */
    @ApiOperation(value = "条件查询用户所有的文章", notes = "条件查询用户所有的文章", httpMethod = "GET")
    @GetMapping("/user/query")
    GraceJsonResult queryUserArticles(@RequestParam String userId,
                                      @RequestParam String keyword,
                                      @RequestParam Integer categoryId,
                                      @RequestParam Integer status,
                                      @RequestParam Date startDate,
                                      @RequestParam Date endDate,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize);

    /**
     * 用户删除文章
     *
     * @param userId    用户id
     * @param articleId 文章id
     * @return GraceJsonResult
     */
    @ApiOperation(value = "用户删除文章", notes = "用户删除文章", httpMethod = "GET")
    @GetMapping("/user/delete")
    GraceJsonResult delete(@RequestParam String userId,
                           @RequestParam String articleId);

    /**
     * 用户撤回文章
     *
     * @param userId    用户id
     * @param articleId 文章id
     * @return GraceJsonResult
     */
    @ApiOperation(value = "用户撤回文章", notes = "用户撤回文章", httpMethod = "GET")
    @GetMapping("/user/withdraw")
    GraceJsonResult withdraw(@RequestParam String userId,
                             @RequestParam String articleId);

    /**
     * 管理员查询所有的文章列表
     *
     * @param status       文章的状态
     * @param page         page 起始分页
     * @param pageSize     pageSize 每页的数目
     * @param deleteStatus 文章的逻辑状态(0:未删除 1: 已删除)
     * @return PagedGridResult
     */
    @ApiOperation(value = "管理员查询用户的所有文章列表", notes = "管理员查询用户的所有文章列表", httpMethod = "GET")
    @GetMapping("/admin/query")
    GraceJsonResult query(@RequestParam Integer status,
                          @RequestParam Integer page,
                          @RequestParam Integer pageSize,
                          @RequestParam Integer deleteStatus);

    /**
     * 管理员对文章进行审核
     *
     * @param articleId 文章id
     * @param passOrNot 文章通过或者不通过 {@link YesOrNo}
     * @return GraceJsonResult
     */
    @ApiOperation(value = "管理员对文章进行审核", notes = "管理员对文章进行审核", httpMethod = "POST")
    @PostMapping("/admin/doReview")
    GraceJsonResult doReview(@RequestParam String articleId,
                             @RequestParam Integer passOrNot);

    /**
     * 重新对文章进行审核
     *
     * @param articleId 文章id
     * @return GraceJsonResult
     */
    @ApiOperation(value = "重新对文章进行审核", notes = "重新对文章进行审核", httpMethod = "POST")
    @PostMapping("/admin/reReview")
    GraceJsonResult reReview(@RequestParam String articleId);


    /**
     * 管理员对文章进行删除(物理删除)
     *
     * @param articleId 文章id
     * @return GraceJsonResult
     */
    @ApiOperation(value = "管理员对文章进行删除(delete sql)", notes = "管理员对文章进行删除(delete sql)", httpMethod = "GET")
    @GetMapping("/admin/delete/{id}")
    GraceJsonResult delete(@PathVariable("id") String articleId);

    /**
     * 管理员撤回用户对文章的删除
     *
     * @param articleId 文章id
     * @return GraceJsonResult
     */
    @ApiOperation(value = "管理员撤回用户对文章的删除", notes = "管理员撤回用户对文章的删除", httpMethod = "GET")
    @GetMapping("/admin/withdrawDelete")
    GraceJsonResult withdrawDelete(@RequestParam String articleId);
}
