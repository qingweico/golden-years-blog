package cn.qingweico.api.controller.article;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.pojo.bo.NewArticleBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/11
 */
@Api(value = "文章发布相关的接口定义", tags = {"文章发布相关的接口定义"})
@RequestMapping("article")
public interface ArticleControllerApi {


    @ApiOperation(value = "发布新文章", notes = "发布新文章", httpMethod = "POST")
    @PostMapping("/user/publish")
    GraceJsonResult createArticle(@RequestBody @Valid NewArticleBO newArticleBO);

    @ApiOperation(value = "查询用户所有的文章", notes = "查询用户所有的文章", httpMethod = "POST")
    @PostMapping("/user/query")
    GraceJsonResult queryUserArticles(@RequestParam String userId,
                                    @RequestParam String keyword,
                                    @RequestParam Integer status,
                                    @RequestParam Date startDate,
                                    @RequestParam Date endDate,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize);

    @ApiOperation(value = "用户删除文章", notes = "用户删除文章", httpMethod = "POST")
    @PostMapping("/user/delete")
    GraceJsonResult delete(@RequestParam String userId,
                           @RequestParam String articleId);

    @ApiOperation(value = "用户撤回文章", notes = "用户撤回文章", httpMethod = "POST")
    @PostMapping("/user/withdraw")
    GraceJsonResult withdraw(@RequestParam String userId,
                             @RequestParam String articleId);

    @ApiOperation(value = "管理员查询用户的所有文章列表", notes = "管理员查询用户的所有文章列表", httpMethod = "POST")
    @PostMapping("/admin/query")
    GraceJsonResult queryAll(@RequestParam Integer status,
                             @RequestParam Integer page,
                             @RequestParam Integer pageSize,
                             @RequestParam Integer deleteStatus);

    @ApiOperation(value = "管理员对文章进行审核", notes = "管理员对文章进行审核", httpMethod = "POST")
    @PostMapping("/admin/doReview")
    GraceJsonResult doReview(@RequestParam String articleId,
                             @RequestParam Integer passOrNot);

    @ApiOperation(value = "重新对文章进行审核", notes = "重新对文章进行审核", httpMethod = "POST")
    @PostMapping("/admin/reReview")
    GraceJsonResult reReview(@RequestParam String articleId);


    @ApiOperation(value = "管理员对文章进行删除(delete sql)", notes = "管理员对文章进行删除(delete sql)", httpMethod = "POST")
    @PostMapping("/admin/delete")
    GraceJsonResult delete(@RequestParam String articleId);

    @ApiOperation(value = "管理员撤回用户对文章的删除", notes = "管理员撤回用户对文章的删除", httpMethod = "POST")
    @PostMapping("/admin/withdrawDelete")
    GraceJsonResult withdrawDelete(@RequestParam String articleId);
}
