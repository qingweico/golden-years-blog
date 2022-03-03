package cn.qingweico.api.controller.article;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.pojo.bo.CommentReplyBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author zqw
 * @date: 2021/9/13
 */
@Api(value = "文章评论相关的接口定义", tags = {"文章评论相关的接口定义"})
@RequestMapping("comment")
public interface CommentControllerApi {
    /**
     * 发布新评论
     *
     * @param commentReplyBO CommentReplyBO
     * @return GraceJsonResult
     */
    @ApiOperation(value = "发布新评论", notes = "发布新评论", httpMethod = "POST")
    @PostMapping("/publish")
    GraceJsonResult publish(@RequestBody @Valid CommentReplyBO commentReplyBO);

    /**
     * 用户评论数查询
     *
     * @param articleId 文章id
     * @return GraceJsonResult
     */
    @ApiOperation(value = "用户评论数查询", notes = "用户评论数查询", httpMethod = "GET")
    @GetMapping("/counts")
    GraceJsonResult getCounts(@RequestParam String articleId);

    /**
     * 查看文章的评论列表
     *
     * @param articleId 文章id
     * @param page      起始分页
     * @param pageSize  每页数量
     * @return GraceJsonResult
     */
    @ApiOperation(value = "查看文章的评论列表", notes = "查看文章的评论列表", httpMethod = "GET")
    @GetMapping("/list")
    GraceJsonResult list(@RequestParam String articleId,
                         @RequestParam Integer page,
                         @RequestParam Integer pageSize);

    /**
     * 作家查看我的评论管理列表
     *
     * @param articleId 文章id
     * @param page      起始分页
     * @param pageSize  每页数量
     * @return GraceJsonResult
     */
    @ApiOperation(value = "作家查看我的评论管理列表", notes = "作家查看我的评论管理列表", httpMethod = "POST")
    @PostMapping("/user/list")
    GraceJsonResult userCommentList(@RequestParam String articleId,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize);


    /**
     * 作者删除评论
     *
     * @param articleId 文章id
     * @param commentId 评论id
     * @return GraceJsonResult
     */
    @ApiOperation(value = "作者删除评论", notes = "作者删除评论", httpMethod = "POST")
    @PostMapping("/user/delete")
    GraceJsonResult deleteComment(@RequestParam String articleId,
                                  @RequestParam String commentId);


}
