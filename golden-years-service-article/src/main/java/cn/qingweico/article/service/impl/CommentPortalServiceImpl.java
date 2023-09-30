package cn.qingweico.article.service.impl;

import cn.qingweico.entity.Comments;
import cn.qingweico.entity.model.ArticleDetail;
import cn.qingweico.entity.model.CommentReply;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConst;
import cn.qingweico.global.RedisConst;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.article.mapper.CommentsMapper;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.article.service.CommentPortalService;
import cn.qingweico.result.Response;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.SnowflakeIdWorker;
import cn.qingweico.util.redis.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

import java.util.*;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Service
public class CommentPortalServiceImpl extends BaseService implements CommentPortalService {


    @Resource
    private CommentsMapper commentsMapper;

    @Resource
    private ArticlePortalService articlePortalService;

    @Resource
    private RedisCache redisCache;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void publishComment(CommentReply commentReply) {
        String articleId = commentReply.getArticleId();
        String userId = commentReply.getUserId();
        String content = commentReply.getContent();
        String pid = commentReply.getParent();
        ArticleDetail articleDetail = articlePortalService.queryDetail(articleId);

        Comments comments = new Comments();
        comments.setId(SnowflakeIdWorker.nextId());
        comments.setAuthorId(articleDetail.getAuthorId());
        comments.setArticleId(articleId);
        comments.setContent(content);
        comments.setParentId(pid);
        comments.setCreateTime(DateUtils.nowDateTime());
        if (commentsMapper.insert(comments) > 0) {
            // 评论数累加
            redisCache.increment(RedisConst.REDIS_ARTICLE_COMMENT_COUNTS + SysConst.SYMBOL_COLON + articleId, 1);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public PagedResult queryArticleComments(String articleId,
                                            Integer page,
                                            Integer pageSize) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(SysConst.ARTICLE_ID, articleId);
        // TODO 评论树
        commentsMapper.queryArticleCommentList(map);
        return new PagedResult();
    }

    @Override
    public PagedResult queryUserComments(String authorId,
                                         Integer page,
                                         Integer pageSize) {
        LambdaQueryWrapper<Comments> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Comments::getCreateTime);
        wrapper.eq(Comments::getAuthorId, authorId);
        List<Comments> res = commentsMapper.selectList(wrapper);
        return setterPagedGrid(res, page);
    }

    @Override
    public void delete(String commentId) {
        commentsMapper.deleteById(commentId);
    }
}
