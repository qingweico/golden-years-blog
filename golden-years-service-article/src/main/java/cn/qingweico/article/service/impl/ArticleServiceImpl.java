package cn.qingweico.article.service.impl;

import cn.qingweico.api.config.RabbitMqConfig;
import cn.qingweico.api.config.RabbitMqDelayConfig;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.service.ArticlePortalService;
import cn.qingweico.article.service.ArticleService;
import cn.qingweico.article.service.TagService;
import cn.qingweico.enums.ArticleAppointType;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConf;
import cn.qingweico.global.SysConf;
import cn.qingweico.pojo.Tag;
import cn.qingweico.pojo.vo.ArticleAdminVO;
import cn.qingweico.pojo.vo.CenterArticleVO;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.bo.NewArticleBO;
import cn.qingweico.pojo.eo.ArticleEo;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.client.gridfs.GridFSBucket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
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
    public GridFSBucket gridFsBucket;

    private static final Integer ARTICLE_SUMMARY = 200;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void createArticle(NewArticleBO newArticleBO) {

        String articleId = sid.nextShort();
        Article article = new Article();
        // 文章概要
        String summary = getArticleSummary(newArticleBO.getContent());
        BeanUtils.copyProperties(newArticleBO, article);
        article.setId(articleId);
        article.setCategoryId(newArticleBO.getCategoryId());
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.type);
        article.setCommentCounts(0);
        article.setReadCounts(0);
        article.setCollectCounts(0);
        article.setIsDelete(YesOrNo.NO.type);
        article.setSummary(summary);
        article.setInfluence(0);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
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
        if (article.getIsAppoint().equals(ArticleAppointType.TIMING.type)) {
            article.setCreateTime(newArticleBO.getCreateTime());
            // 发送延迟消息到mq队列
            Date willPublishTime = newArticleBO.getCreateTime();
            Date now = new Date();

            long delay = willPublishTime.getTime() - now.getTime();
            MessagePostProcessor messagePostProcessor = (message) -> {
                message.getMessageProperties().
                        setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setDelay((int) (delay));
                return message;
            };
            rabbitTemplate.convertAndSend(RabbitMqDelayConfig.EXCHANGE_DELAY,
                    SysConf.ARTICLE_DELAY_PUBLISH_SUCCESS_DO,
                    articleId,
                    messagePostProcessor);
        } else if (article.getIsAppoint().equals(ArticleAppointType.IMMEDIATELY.type)) {
            article.setCreateTime(new Date());
        }

        int res = articleMapper.insert(article);
        if (res != 1) {
            GraceException.error(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
        }
        updateArticleStatus(articleId, ArticleReviewStatus.REVIEWING.type);
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
        article.setIsAppoint(ArticleAppointType.IMMEDIATELY.type);
        if (articleMapper.updateByPrimaryKeySelective(article) < 0) {
            log.error("timelyPublishArticle error");
            GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public PagedGridResult queryUserArticles(String userId,
                                             String keyword,
                                             String categoryId,
                                             Integer status,
                                             Date startDate,
                                             Date endDate,
                                             Integer page,
                                             Integer pageSize) {

        Example example = conditionalQueryArticle(userId, keyword, categoryId, status
                , YesOrNo.NO.type, startDate, endDate);
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        PageInfo<Article> pageInfo = new PageInfo<>(list);
        List<Article> paged = pageInfo.getList();
        List<CenterArticleVO> result = new ArrayList<>();
        for (Article article : paged) {
            CenterArticleVO centerArticleVO = new CenterArticleVO();
            BeanUtils.copyProperties(article, centerArticleVO);
            List<Tag> tagList = articlePortalService.getTagList(article);
            centerArticleVO.setTagList(tagList);
            result.add(centerArticleVO);
        }
        PagedGridResult pgr = new PagedGridResult();
        pgr.setRows(result);
        pgr.setCurrentPage(pageInfo.getPageNum());
        pgr.setRecords(pageInfo.getTotal());
        pgr.setTotalPage(pageInfo.getPages());
        return pgr;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SysConf.ID, articleId);
        Article article = new Article();
        article.setArticleStatus(pendingStatus);

        int res = articleMapper.updateByExampleSelective(article, example);

        if (res != 1) {
            GraceException.error(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

        // 如果审核通过, 则查询article相关的字段并放入es中
        if (ArticleReviewStatus.SUCCESS.type.equals(pendingStatus)) {
            Article result = articleMapper.selectByPrimaryKey(articleId);
            if (ArticleAppointType.IMMEDIATELY.type.equals(result.getIsAppoint())) {
                String summary = getArticleSummary(result.getContent());
                ArticleEo articleEo = new ArticleEo();
                BeanUtils.copyProperties(result, articleEo);
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
                articleEo.setSummary(summary);
                articleEo.setTags(resultTag.toString());
                IndexQuery indexQuery = new IndexQueryBuilder().withObject(articleEo).build();
                elasticsearchTemplate.index(indexQuery);

                // 更新主站带有文章数量的文展类别
                String key = RedisConf.REDIS_ARTICLE_CATEGORY_WITH_ARTICLE_COUNT;
                refreshCache(key);
            }
        }
    }

    @Override
    public void updateArticle(NewArticleBO newArticleBO) {
        Article article = new Article();
        article.setId(newArticleBO.getArticleId());
        article.setUpdateTime(new Date());
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
        if (articleMapper.updateByPrimaryKeySelective(article) <= 0) {
            GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public PagedGridResult query(String keyword,
                                 Integer status,
                                 String categoryId,
                                 String tagId,
                                 Integer deleteStatus,
                                 Date startDateStr,
                                 Date endDateStr,
                                 Integer page,
                                 Integer pageSize) {
        Example example = conditionalQueryArticle(null, keyword, categoryId, status
                , deleteStatus, startDateStr, endDateStr);
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        if (StringUtils.isNotBlank(tagId)) {
            // 标签筛选
            list = filterArticleTag(list, tagId);
        }
        PageInfo<Article> pageInfo = new PageInfo<>(list);
        List<Article> paged = pageInfo.getList();
        List<ArticleAdminVO> result = new ArrayList<>();
        for (Article article : paged) {
            ArticleAdminVO articleAdminVO = new ArticleAdminVO();
            BeanUtils.copyProperties(article, articleAdminVO);
            List<Tag> tagList = articlePortalService.getTagList(article);
            articleAdminVO.setTagList(tagList);
            result.add(articleAdminVO);
        }
        PagedGridResult pgr = new PagedGridResult();
        pgr.setRows(result);
        pgr.setCurrentPage(pageInfo.getPageNum());
        pgr.setRecords(pageInfo.getTotal());
        pgr.setTotalPage(pageInfo.getPages());
        return pgr;
    }

    private Example conditionalQueryArticle(String userId,
                                            String keyword,
                                            String categoryId,
                                            Integer status,
                                            Integer deleteStatus,
                                            Date startDate,
                                            Date endDate) {
        Example example = new Example(Article.class);
        example.orderBy(SysConf.CREATE_TIME).desc();
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(userId)) {
            criteria.andEqualTo(SysConf.AUTHOR_ID, userId);
        }
        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike(SysConf.TITLE, SysConf.DELIMITER_PERCENT + keyword + SysConf.DELIMITER_PERCENT);
        }

        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo(SysConf.ARTICLE_STATUS, status);
        }
        if (StringUtils.isNotBlank(categoryId)) {
            criteria.andEqualTo(SysConf.CATEGORY_ID, categoryId);
        }
        if (deleteStatus != null) {
            criteria.andEqualTo(SysConf.IS_DELETE, deleteStatus);
        }
        if (startDate != null) {
            criteria.andGreaterThanOrEqualTo(SysConf.CREATE_TIME, startDate);
        }
        if (endDate != null) {
            criteria.andLessThanOrEqualTo(SysConf.CREATE_TIME, endDate);
        }
        return example;

    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteArticle(String articleId) {
        // 删除已通过审核的文章, 还需删除gridFs和es中的数据
        Integer articleStatus = articleMapper.selectByPrimaryKey(articleId).getArticleStatus();
        if (ArticleReviewStatus.SUCCESS.type.equals(articleStatus)) {
            elasticsearchTemplate.delete(ArticleEo.class, articleId);
        }
        articleMapper.deleteByPrimaryKey(articleId);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void reReview(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.type);
        articleMapper.updateByPrimaryKeySelective(article);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void withdrawDelete(String articleId) {
        Example articleExample = new Example(Article.class);
        Article article = new Article();
        article.setId(articleId);
        article.setIsDelete(YesOrNo.NO.type);
        articleMapper.updateByExampleSelective(article, articleExample);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteArticle(String userId, String articleId) {
        Example articleExample = makeExampleCriteria(userId, articleId);
        Article pending = new Article();
        pending.setIsDelete(YesOrNo.YES.type);
        // 删除未审核过的文章; 直接设置is_delete = 1
        int result = articleMapper.updateByExampleSelective(pending, articleExample);
        if (result != 1) {
            GraceException.error(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
        }
    }

    @SuppressWarnings("unused")
    @Deprecated
    private void deleteArticleHtmlForGridFs(String articleId) {
        // 查询文章的mongoId
        Article article = articleMapper.selectByPrimaryKey(articleId);
        String articleMongoId = article.getMongoFileId();

        // 删除GridFs上文章的html文件
        gridFsBucket.delete(new ObjectId(articleMongoId));

        // 删除消费端的文章html文件
        doDeleteArticleHtmlByMq(articleId);
    }

    private void doDeleteArticleHtmlByMq(String articleId) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_ARTICLE,
                "article.delete.do", articleId);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void withdrawArticle(String userId, String articleId) {
        Example articleExample = makeExampleCriteria(userId, articleId);
        Article pending = new Article();
        pending.setArticleStatus(ArticleReviewStatus.WITHDRAW.type);
        int result = articleMapper.updateByExampleSelective(pending, articleExample);
        if (result != 1) {
            GraceException.error(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
        }
        elasticsearchTemplate.delete(ArticleEo.class, articleId);

    }
    private Example makeExampleCriteria(String userId, String articleId) {
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = articleExample.createCriteria();
        criteria.andEqualTo(SysConf.AUTHOR_ID, userId);
        criteria.andEqualTo(SysConf.ID, articleId);
        return articleExample;
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
