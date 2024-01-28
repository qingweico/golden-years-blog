package cn.qingweico.article.service.impl;

import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.service.ArticleService;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.entity.Article;
import cn.qingweico.entity.model.NewArticleBO;
import cn.qingweico.enums.ArticleAppointType;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.redis.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/11
 */
@Slf4j
@Service
public class ArticleServiceImpl extends BaseService implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private RedisUtil redisUtil;


    private static final Integer ARTICLE_SUMMARY = 200;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void createArticle(NewArticleBO newArticleBO) {

        String articleId = "";
        Article article = new Article();
        // 文章概要
        String summary = getArticleSummary(newArticleBO.getContent());
        BeanUtils.copyProperties(newArticleBO, article);
        article.setId(articleId);
        article.setCategoryId(newArticleBO.getCategoryId());
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.getVal());
        article.setCommentCounts(0);
        article.setReadCounts(0);
        article.setCollectCounts(0);
        article.setIsDelete(YesOrNo.NO.getVal());
        article.setSummary(summary);
        article.setInfluence(0);
        article.setCreateTime(DateUtils.nowDateTime());
        article.setUpdateTime(DateUtils.nowDateTime());
        // 定时发布文章
        if (article.getIsAppoint().equals(ArticleAppointType.TIMING.getVal())) {
            article.setCreateTime(newArticleBO.getCreateTime());
        } else if (article.getIsAppoint().equals(ArticleAppointType.IMMEDIATELY.getVal())) {
            article.setCreateTime(DateUtils.nowDateTime());
        }

        int res = articleMapper.insert(article);
        if (res != 1) {
            GraceException.error(Response.ARTICLE_CREATE_ERROR);
        }
        updateArticleStatus(articleId, ArticleReviewStatus.REVIEWING.getVal());
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void timedPublishArticle() {
        articleMapper.timedPublishArticle();
    }


    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void timelyPublishArticle(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(ArticleAppointType.IMMEDIATELY.getVal());
        if (articleMapper.updateById(article) < 0) {
            log.error("timelyPublishArticle error");
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public PagedResult queryUserArticles(String userId,
                                         String keyword,
                                         String categoryId,
                                         Integer status,
                                         Date startDate,
                                         Date endDate,
                                         Integer page,
                                         Integer pageSize) {

        LambdaQueryWrapper<Article> lambdaQueryWrapper = conditionalQueryArticle(userId, keyword, categoryId, status
                , YesOrNo.NO.getVal(), startDate, endDate);
        // 分页
        List<Article> list = articleMapper.selectList(lambdaQueryWrapper);
        return new PagedResult();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
        Article article = new Article();
        article.setId(articleId);
        article.setArticleStatus(pendingStatus);
        articleMapper.updateById(article);
        // 如果审核通过, 则查询article相关的字段并放入es中
        if (ArticleReviewStatus.APPROVED.getVal().equals(pendingStatus)) {
            Article result = articleMapper.selectById(articleId);
            if (ArticleAppointType.IMMEDIATELY.getVal().equals(result.getIsAppoint())) {
                // 更新主站带有文章数量的文展类别
                String key = RedisConst.REDIS_ARTICLE_CATEGORY_WITH_ARTICLE_COUNT;
                redisUtil.clearCache(key);
            }
        }
    }

    @Override
    public void updateArticle(NewArticleBO newArticleBO) {
        Article article = new Article();
        article.setId(newArticleBO.getArticleId());
        article.setUpdateTime(DateUtils.nowDateTime());
        BeanUtils.copyProperties(newArticleBO, article);
        // 假如文章的状态为未通过或者为已撤回, 则修改文章后文章的状态变为审核中
        if (ArticleReviewStatus.FAILED.getVal().equals(newArticleBO.getArticleStatus()) ||
                ArticleReviewStatus.WITHDRAW.getVal().equals(newArticleBO.getArticleStatus()
                )) {
            article.setArticleStatus(ArticleReviewStatus.REVIEWING.getVal());
        }
        articleMapper.updateById(article);
    }

    @Override
    public PagedResult query(String keyword,
                             Integer status,
                             String categoryId,
                             String tagId,
                             Integer deleteStatus,
                             Date startDateStr,
                             Date endDateStr,
                             Integer page,
                             Integer pageSize) {
        LambdaQueryWrapper<Article> lambdaQueryWrapper = conditionalQueryArticle(null, keyword, categoryId, status
                , deleteStatus, startDateStr, endDateStr);
        // 分页
        List<Article> list = articleMapper.selectList(lambdaQueryWrapper);
        return null;
    }

    private LambdaQueryWrapper<Article> conditionalQueryArticle(String userId,
                                                                String keyword,
                                                                String categoryId,
                                                                Integer status,
                                                                Integer deleteStatus,
                                                                Date startDate,
                                                                Date endDate) {
        LambdaQueryWrapper<Article> lwq = new LambdaQueryWrapper<>();
        lwq.orderByDesc(Article::getCreateTime);
        if (StringUtils.isNotEmpty(userId)) {
            lwq.eq(Article::getAuthorId, userId);
        }
        if (StringUtils.isNotEmpty(keyword)) {
            lwq.eq(Article::getTitle, SysConst.DELIMITER_PERCENT + keyword + SysConst.DELIMITER_PERCENT);
        }

        if (ArticleReviewStatus.isValidArticleStatus(status)) {
            lwq.eq(Article::getArticleStatus, status);
        }
        if (StringUtils.isNotEmpty(categoryId)) {
            lwq.eq(Article::getCategoryId, categoryId);
        }
        if (deleteStatus != null) {
            lwq.eq(Article::getIsDelete, deleteStatus);
        }
        if (startDate != null) {
            lwq.ge(Article::getCreateTime, startDate);
        }
        if (endDate != null) {
            lwq.le(Article::getCreateTime, endDate);
        }
        return lwq;

    }

    @Override
    public void deleteArticle(String articleId) {
        articleMapper.deleteById(articleId);
    }

    @Override
    public void reReview(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.getVal());
        articleMapper.updateById(article);
    }

    @Override
    public void withdrawDelete(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsDelete(YesOrNo.NO.getVal());
        articleMapper.updateById(article);
    }

    @Override
    public void deleteArticle(String userId, String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsDelete(YesOrNo.YES.getVal());
        articleMapper.updateById(article);
    }


    @Override
    public void withdrawArticle(String userId, String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setArticleStatus(ArticleReviewStatus.WITHDRAW.getVal());
        articleMapper.updateById(article);

    }

    private String filterHtmlTag(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        //过滤html标签
        html = html.replaceAll("<[\\s\\S]*?>", "");
        html = html.replaceAll(" ", "");
        //过滤空格, 并保留一个空格
        html = html.replaceAll("\\s+", " ");

        return html;
    }

    private String getArticleSummary(String htmlContent) {
        String summary;
        String contentText = filterHtmlTag(htmlContent);
        if (contentText.length() <= ARTICLE_SUMMARY) {
            summary = contentText;
        } else {
            summary = contentText.substring(0, ARTICLE_SUMMARY);
        }
        return summary;
    }
}
