package cn.qingweico.article.controller;

import cn.qingweico.core.base.BaseController;
import cn.qingweico.article.service.CommentPortalService;
import cn.qingweico.result.Result;
import cn.qingweico.pojo.bo.CommentReplyBO;
import cn.qingweico.pojo.vo.UserBasicInfoVO;
import cn.qingweico.result.Response;
import cn.qingweico.util.PagedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Api(value = "文章评论相关的接口定义", tags = {"文章评论相关的接口定义"})
@RequestMapping("comment")
@RestController
public class CommentController extends BaseController {


    @Resource
    private CommentPortalService commentPortalService;
    @Resource
    private ArticlePortalController articlePortalController;

    @ApiOperation(value = "发布新评论", notes = "发布新评论", httpMethod = "POST")
    @PostMapping("/publish")
    public Result publish(@RequestBody @Valid CommentReplyBO commentReplyBO) {

        String userId = commentReplyBO.getCommentUserId();
        Set<String> set = new HashSet<>();
        set.add(userId);
        UserBasicInfoVO userVO = articlePortalController.getUserInfoListByIdsClient(set).get(0);
        // 获得发表评论的用户昵称
        String nickname = userVO.getNickname();
        // 获得发表评论的用户头像
        String face = userVO.getFace();
        commentReplyBO.setCommentUserNickname(nickname);
        commentReplyBO.setCommentUserFace(face);
        commentPortalService.publishComment(commentReplyBO);
        return Result.r(Response.COMMENT_SUCCESS);
    }

    @ApiOperation(value = "查看文章的评论列表", notes = "查看文章的评论列表", httpMethod = "GET")
    @GetMapping("/list")
    public Result list(@RequestParam String articleId,
                       @RequestParam Integer page,
                       @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedResult res = commentPortalService.queryArticleComments(articleId,
                page,
                pageSize);
        return Result.ok(res);
    }

    @ApiOperation(value = "作者查看我的评论管理列表", notes = "作者查看我的评论管理列表", httpMethod = "GET")
    @GetMapping("/user/list")
    public Result userCommentList(@RequestParam String userId,
                                  @RequestParam Integer page,
                                  @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedResult res = commentPortalService.queryUserComments(userId,
                page,
                pageSize);
        return Result.ok(res);
    }

    @ApiOperation(value = "作者删除评论", notes = "作者删除评论", httpMethod = "POST")
    @PostMapping("/user/delete/{id}")
    public Result deleteComment(@PathVariable("id") String commentId) {
        commentPortalService.delete(commentId);
        return Result.r(Response.DELETE_SUCCESS);
    }
}
