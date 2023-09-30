package cn.qingweico.article.service.impl;

import cn.qingweico.core.config.RabbitMqDelayConfig;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.article.service.ArticleService;
import cn.qingweico.article.service.TagService;
import cn.qingweico.entity.Article;
import cn.qingweico.entity.Tag;
import cn.qingweico.entity.model.ArticleElastic;
import cn.qingweico.entity.model.CreativeCenterArticle;
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
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

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
    private TagService tagService;

    @Resource
    private ArticlePortalService articlePortalService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ElasticsearchTemplate esTemplate;

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
        List<Tag> tags = newArticleBO.getTags();
        StringBuilder tagsString = new StringBuilder();
        // 标签提取
        tagsString.append("[");
        for (Tag tag : tags) {

            // 用户自定义标签
            if (tag.getId() == null) {
                String personalTagId = tagService.addPersonalTag(tag, newArticleBO.getAuthorId());
                tagsString.append("\"");
                tagsString.append(personalTagId);
            } else {
                // 系统标签
                tagsString.append("\"");
                tagsString.append(tag.getId());
            }
            tagsString.append("\",");
        }
        tagsString.append("]");
        tagsString.delete(tagsString.length() - 2, tagsString.length() - 1);
        article.setTags(tagsString.toString());
        // 定时发布文章
        if (article.getIsAppoint().equals(ArticleAppointType.TIMING.getVal())) {
            article.setCreateTime(newArticleBO.getCreateTime());
            // 发送延迟消息到mq队列
            LocalDateTime willPublishTime = newArticleBO.getCreateTime();
            long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long delay = willPublishTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - now;
            MessagePostProcessor messagePostProcessor = (message) -> {
                message.getMessageProperties().
                        setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setDelay((int) (delay));
                return message;
            };
            rabbitTemplate.convertAndSend(RabbitMqDelayConfig.EXCHANGE_DELAY,
                    SysConst.ARTICLE_DELAY_PUBLISH_SUCCESS_DO,
                    articleId,
                    messagePostProcessor);
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
        List<CreativeCenterArticle> result = new ArrayList<>();
        for (Article article : list) {
            CreativeCenterArticle creativeCenterArticle = new CreativeCenterArticle();
            BeanUtils.copyProperties(article, creativeCenterArticle);
            List<Tag> tagList = articlePortalService.getTagList(article);
            creativeCenterArticle.setTagList(tagList);
            result.add(creativeCenterArticle);
        }
        return new PagedResult();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
        Article article = new Article();
        article.setId(articleId);
        article.setArticleStatus(pendingStatus);
        int res = articleMapper.updateById(article);
        // 如果审核通过, 则查询article相关的字段并放入es中
        if (ArticleReviewStatus.APPROVED.getVal().equals(pendingStatus)) {
            Article result = articleMapper.selectById(articleId);
            if (ArticleAppointType.IMMEDIATELY.getVal().equals(result.getIsAppoint())) {
                String summary = getArticleSummary(result.getContent());
                ArticleElastic articleElastic = new ArticleElastic();
                BeanUtils.copyProperties(result, articleElastic);
                // 获取文章标签id集合
                String[] tagIds = result.getTags().replace("[", "")
                        .replace("]", "")
                        .replace("\"", "")
                        .split(",");
                StringBuilder resultTag = new StringBuilder();
                for (String id : tagIds) {
                    resultTag.append(id);
                    resultTag.append(" ");
                }
                resultTag.delete(resultTag.length() - 1, resultTag.length());
                articleElastic.setSummary(summary);
                articleElastic.setTags(resultTag.toString());
                IndexQuery indexQuery = new IndexQueryBuilder().withObject(articleElastic).build();
                esTemplate.index(indexQuery);

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
        List<Tag> tags = newArticleBO.getTags();
        StringBuilder tagsString = new StringBuilder();
        tagsString.append("[");
        for (Tag tag : tags) {
            tagsString.append("\"");
            tagsString.append(tag.getId());
            tagsString.append("\",");
        }
        tagsString.append("]");
        tagsString.delete(tagsString.length() - 2, tagsString.length() - 1);
        article.setTags(tagsString.toString());
        // 假如文章的状态为未通过或者为已撤回, 则修改文章后文章的状态变为审核中
        if (ArticleReviewStatus.FAILED.getVal().equals(newArticleBO.getArticleStatus()) ||
                ArticleReviewStatus.WITHDRAW.getVal().equals(newArticleBO.getArticleStatus()
                )) {
            article.setArticleStatus(ArticleReviewStatus.REVIEWING.getVal());
        }
        if (articleMapper.updateById(article) <= 0) {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
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
        if (StringUtils.isNotBlank(tagId)) {
            // 标签筛选
            list = filterArticleTag(list, tagId);
        }
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

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteArticle(String articleId) {
        // 删除已通过审核的文章, 还需删除es中的数据
        Integer articleStatus = articleMapper.selectById(articleId).getArticleStatus();
        if (ArticleReviewStatus.APPROVED.getVal().equals(articleStatus)) {
            esTemplate.delete(ArticleElastic.class, articleId);
        }
        articleMapper.deleteById(articleId);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void reReview(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.getVal());
        articleMapper.updateById(article);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void withdrawDelete(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsDelete(YesOrNo.NO.getVal());
        articleMapper.updateById(article);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteArticle(String userId, String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsDelete(YesOrNo.YES.getVal());
        // 删除未审核过的文章; 直接设置is_delete = 1
        int result = articleMapper.updateById(article);
        if (result != 1) {
            GraceException.error(Response.ARTICLE_DELETE_ERROR);
        }
    }


    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void withdrawArticle(String userId, String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setArticleStatus(ArticleReviewStatus.WITHDRAW.getVal());
        articleMapper.updateById(article);
        esTemplate.delete(ArticleElastic.class, articleId);

    }

    private String filterHtmlTag(String html) {
        if (html == null || "".equals(html)) {
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
