package cn.qingweico.api.controller.article;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.pojo.bo.CommentReplyBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author:qiming
 * @date: 2021/9/13
 */
@Api(value = "文章评论相关的接口定义", tags = {"文章评论相关的接口定义"})
@RequestMapping("comment")
public interface CommentControllerApi {
    @ApiOperation(value = "发布新评论", notes = "发布新评论", httpMethod = "POST")
    @PostMapping("/publish")
    GraceJsonResult publish(@RequestBody @Valid CommentReplyBO commentReplyBO);

    @ApiOperation(value = "用户评论数查询", notes = "用户评论数查询", httpMethod = "GET")
    @GetMapping("/counts")
    GraceJsonResult getCounts(@RequestParam String articleId);

    @ApiOperation(value = "查看文章的评论列表", notes = "查看文章的评论列表", httpMethod = "GET")
    @GetMapping("/list")
    GraceJsonResult list(@RequestParam String articleId,
                         @RequestParam Integer page,
                         @RequestParam Integer pageSize);


    @ApiOperation(value = "作者查看我的评论管理列表", notes = "查看我的评论管理列表", httpMethod = "POST")
    @PostMapping("/user/list")
    GraceJsonResult userCommentList(@RequestParam String writerId,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize);


    @ApiOperation(value = "作者删除评论", notes = "作者删除评论", httpMethod = "POST")
    @PostMapping("/user/delete")
    GraceJsonResult deleteComment(@RequestParam String writerId,
                                  @RequestParam String commentId);


}
