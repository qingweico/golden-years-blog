package cn.qingweico.article.service.impl;

import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConf;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.Comments;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.CommentsMapper;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.article.service.CommentPortalService;
import cn.qingweico.pojo.bo.CommentReplyBO;
import cn.qingweico.pojo.bo.Reply;
import cn.qingweico.pojo.bo.Visitor;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.pojo.vo.CommentVo;
import cn.qingweico.pojo.vo.CommentsVO;
import cn.qingweico.result.Response;
import cn.qingweico.util.PagedResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

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
    public void publishComment(CommentReplyBO commentReplyBO) {
        String articleId = commentReplyBO.getArticleId();
        String userId = commentReplyBO.getCommentUserId();
        String nickname = commentReplyBO.getCommentUserNickname();
        String face = commentReplyBO.getCommentUserFace();
        String content = commentReplyBO.getContent();
        String fatherCommentId = commentReplyBO.getParent();
        ArticleDetailVO articleDetailVO = articlePortalService.queryDetail(articleId);

        Comments comments = new Comments();
        comments.setId(sid.nextShort());
        comments.setAuthor(articleDetailVO.getAuthorId());
        comments.setArticleCover(articleDetailVO.getArticleCover());
        comments.setArticleTitle(articleDetailVO.getTitle());

        comments.setArticleId(articleId);
        comments.setCommentUserId(userId);
        comments.setCommentUserNickname(nickname);
        comments.setCommentUserFace(face);
        comments.setContent(content);
        comments.setFatherId(fatherCommentId);
        comments.setCreateTime(new Date());
        if (commentsMapper.insert(comments) > 0) {
            // 评论数累加
            redisTemplate.increment(RedisConf.REDIS_ARTICLE_COMMENT_COUNTS + SysConf.SYMBOL_COLON + articleId, 1);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public PagedResult queryArticleComments(String articleId,
                                            Integer page,
                                            Integer pageSize) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(SysConf.ARTICLE_ID, articleId);
        PageHelper.startPage(page, pageSize);
        List<CommentsVO> commentList = commentsMapper.queryArticleCommentList(map);
        PageInfo<CommentsVO> pageInfo = new PageInfo<>(commentList);
        List<CommentsVO> paged = pageInfo.getList();
        List<CommentVo> list = new ArrayList<>();
        for (CommentsVO commentsVO : paged) {
            CommentVo commentVo = new CommentVo();
            BeanUtils.copyProperties(commentsVO, commentVo);
            Reply reply = new Reply();
            String replyAvatar = commentsVO.getReplyAvatar();
            String replyName = commentsVO.getReplyName();
            if (replyAvatar != null) {
                reply.setAvatar(replyAvatar);
            }
            if (replyName != null) {
                reply.setName(replyName);
            }
            Visitor visitor = new Visitor();
            visitor.setAvatar(commentsVO.getVisitorAvatar());
            visitor.setName(commentsVO.getVisitorName());
            commentVo.setReply(reply);
            commentVo.setVisitor(visitor);
            list.add(commentVo);
        }
        PagedResult pgr = new PagedResult();
        pgr.setRows(list);
        pgr.setCurrentPage(pageInfo.getPageNum());
        pgr.setRecords(pageInfo.getTotal());
        pgr.setTotalPage(pageInfo.getPages());
        return pgr;
    }

    @Override
    public PagedResult queryUserComments(String authorId,
                                         Integer page,
                                         Integer pageSize) {
        Example example = new Example(Comments.class);
        example.orderBy(SysConf.CREATE_TIME).desc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SysConf.AUTHOR, authorId);
        PageHelper.startPage(page, pageSize);
        List<Comments> res = commentsMapper.selectByExample(example);
        return setterPagedGrid(res, page);
    }

    @Override
    public void delete(String commentId) {
        Comments comments = new Comments();
        comments.setId(commentId);
        commentsMapper.deleteByPrimaryKey(comments);
    }
}
