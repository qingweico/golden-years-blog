package cn.qingweico.article.controller;

import cn.qingweico.api.controller.BaseController;
import cn.qingweico.api.controller.article.CommentControllerApi;
import cn.qingweico.article.service.CommentPortalService;
import cn.qingweico.global.Constants;
import cn.qingweico.global.RedisConf;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.pojo.bo.CommentReplyBO;
import cn.qingweico.pojo.vo.UserBasicInfoVO;
import cn.qingweico.util.PagedGridResult;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zqw
 * @date 2021/9/13
 */
@RestController
public class CommentController extends BaseController implements CommentControllerApi {


    @Resource
    private CommentPortalService commentPortalService;
    @Resource
    private ArticlePortalController articlePortalController;

    @Override
    public GraceJsonResult publish(CommentReplyBO commentReplyBO) {

        String userId = commentReplyBO.getCommentUserId();

        Set<String> set = new HashSet<>();
        set.add(userId);

        UserBasicInfoVO userVO = articlePortalController.getUserBasicInfoList(set).get(0);
        // 获得发表评论的用户昵称
        String nickname = userVO.getNickname();
        // 获得发表评论的用户头像
        String face = userVO.getFace();


        commentPortalService.publishComment(commentReplyBO.getArticleId(),
                commentReplyBO.getFatherId(),
                commentReplyBO.getContent(),
                commentReplyBO.getCommentUserId(),
                nickname,
                face);

        return GraceJsonResult.ok();
    }

    @Override
    public GraceJsonResult getCounts(String articleId) {
        int commentCounts = getCountsFromRedis(RedisConf.REDIS_ARTICLE_COMMENT_COUNTS + Constants.SYMBOL_COLON + articleId);
        return GraceJsonResult.ok(commentCounts);
    }

    @Override
    public GraceJsonResult list(String articleId,
                                Integer page,
                                Integer pageSize) {
        if (page == null) {
            page = Constants.COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = Constants.COMMON_PAGE_SIZE;
        }
        PagedGridResult res = commentPortalService.queryArticleComments(articleId,
                page,
                pageSize);
        return GraceJsonResult.ok(res);
    }

    @Override
    public GraceJsonResult userCommentList(String articleId, Integer page, Integer pageSize) {
        if (page == null) {
            page = Constants.COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = Constants.COMMON_PAGE_SIZE;
        }
        PagedGridResult res = commentPortalService.queryWriterComments(articleId,
                page,
                pageSize);
        return GraceJsonResult.ok(res);
    }

    @Override
    public GraceJsonResult deleteComment(String articleId, String commentId) {
        commentPortalService.delete(articleId, commentId);
        return GraceJsonResult.ok();
    }
}