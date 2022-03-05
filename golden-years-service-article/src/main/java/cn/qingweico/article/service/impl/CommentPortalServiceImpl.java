package cn.qingweico.article.service.impl;

import cn.qingweico.global.Constants;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.Comments;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.CommentsMapper;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.article.service.CommentPortalService;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.pojo.vo.CommentsVO;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import org.n3r.idworker.Sid;
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
    private Sid sid;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void publishComment(String articleId,
                               String fatherCommentId,
                               String content,
                               String userId,
                               String nickname,
                               String face) {

        ArticleDetailVO articleDetailVO = articlePortalService.queryDetail(articleId);

        Comments comments = new Comments();
        comments.setId(sid.nextShort());
        comments.setAuthor(articleDetailVO.getAuthorId());
        comments.setArticleCover(articleDetailVO.getArticleCover());
        comments.setArticleTitle(articleDetailVO.getTitle());

        comments.setArticleId(articleId);
        comments.setCommentUserId(userId);
        comments.setCommentUserNickname(nickname);
        comments.setContent(content);
        comments.setFatherId(fatherCommentId);
        comments.setCreateTime(new Date());
        comments.setCommentUserFace(face);

        commentsMapper.insert(comments);

        // 评论数累加
        redisOperator.increment(RedisConf.REDIS_ARTICLE_COMMENT_COUNTS + Constants.SYMBOL_COLON + articleId, 1);
    }

    @Override
    public PagedGridResult queryArticleComments(String articleId,
                                                Integer page,
                                                Integer pageSize) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("articleId", articleId);
        PageHelper.startPage(page, pageSize);
        List<CommentsVO> commentList = commentsMapper.queryArticleCommentList(map);
        return setterPagedGrid(commentList, page);
    }

    @Override
    public PagedGridResult queryUserComments(String author,
                                             Integer page,
                                             Integer pageSize) {
        Comments comments = new Comments();
        comments.setAuthor(author);
        PageHelper.startPage(page, pageSize);
        List<Comments> res = commentsMapper.select(comments);
        return setterPagedGrid(res, page);
    }

   @Override
   public void delete(String userId, String commentId) {
      Comments comments = new Comments();
      comments.setAuthor(userId);
      comments.setId(commentId);
      commentsMapper.delete(comments);
   }
}
