package cn.qingweico.article.service.impl;

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
 * @author:qiming
 * @date: 2021/9/13
 */
@Service
public class CommentPortalServiceImpl extends BaseService implements CommentPortalService {


    @Resource
    private CommentsMapper commentsMapper;

    @Resource
    private ArticlePortalService articlePortalService;

    @Resource
    private Sid sid;

    @Transactional
    @Override
    public void publishComment(String articleId,
                               String fatherCommentId,
                               String content,
                               String userId,
                               String nickName,
                               String face) {

        ArticleDetailVO articleDetailVO = articlePortalService.queryDetail(articleId);

        Comments comments = new Comments();
        comments.setId(sid.nextShort());
        comments.setWriterId(articleDetailVO.getPublishUserId());
        comments.setArticleCover(articleDetailVO.getCover());
        comments.setArticleTitle(articleDetailVO.getTitle());

        comments.setArticleId(articleId);
        comments.setCommentUserId(userId);
        comments.setCommentUserNickname(nickName);
        comments.setContent(content);
        comments.setFatherId(fatherCommentId);
        comments.setCreateTime(new Date());
        comments.setCommentUserFace(face);

        commentsMapper.insert(comments);

        // 评论数累加
        redisOperator.increment(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId, 1);
    }

    @Override
    public PagedGridResult queryArticleComments(String articleId,
                                                Integer page,
                                                Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("articleId", articleId);
        PageHelper.startPage(page, pageSize);
        List<CommentsVO> commentList = commentsMapper.queryArticleCommentList(map);
        return setterPagedGrid(commentList, page);
    }

    @Override
    public PagedGridResult queryWriterComments(String writerId,
                                               Integer page,
                                               Integer pageSize) {
        Comments comments = new Comments();
        comments.setWriterId(writerId);
        PageHelper.startPage(page, pageSize);
        List<Comments> res = commentsMapper.select(comments);
        return setterPagedGrid(res, page);
    }

   @Override
   public void delete(String writerId, String commentId) {
      Comments comments = new Comments();
      comments.setWriterId(writerId);
      comments.setId(commentId);
      commentsMapper.delete(comments);
   }
}
