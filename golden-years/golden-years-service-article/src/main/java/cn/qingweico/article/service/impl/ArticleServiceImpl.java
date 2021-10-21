package cn.qingweico.article.service.impl;

import cn.qingweico.api.config.RabbitMqConfig;
import cn.qingweico.api.config.RabbitMqDelayConfig;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.service.ArticleService;
import cn.qingweico.enums.ArticleAppointType;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.exception.GraceException;
import cn.qingweico.grace.result.ResponseStatusEnum;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.bo.NewArticleBO;
import cn.qingweico.pojo.eo.ArticleEO;
import cn.qingweico.util.PagedGridResult;
import cn.qingweico.util.extend.AliTextReviewUtils;
import com.github.pagehelper.PageHelper;
import com.mongodb.client.gridfs.GridFSBucket;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.n3r.idworker.Sid;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/11
 */
@Service
public class ArticleServiceImpl extends BaseService implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private Sid sid;

    @Resource
    private AliTextReviewUtils aliTextReviewUtils;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    public GridFSBucket gridFSBucket;

    @Transactional
    @Override
    public void createArticle(NewArticleBO newArticleBO) {

        String articleId = sid.nextShort();
        Article article = new Article();
        BeanUtils.copyProperties(newArticleBO, article);

        article.setId(articleId);

        article.setCategoryId(newArticleBO.getCategoryId());
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.type);
        article.setCommentCounts(0);
        article.setReadCounts(0);
        article.setIsDelete(YesOrNo.NO.type);

        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        if (article.getIsAppoint().equals(ArticleAppointType.TIMING.type)) {
            article.setPublishTime(newArticleBO.getPublishTime());
            // 发送延迟消息到mq队列
            Date willPublishTime = newArticleBO.getPublishTime();
            Date now = new Date();

            long delay = willPublishTime.getTime() - now.getTime();
            MessagePostProcessor messagePostProcessor = (message) -> {
                message.getMessageProperties().
                        setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setDelay((int) (delay));
                return message;
            };
            rabbitTemplate.convertAndSend(RabbitMqDelayConfig.EXCHANGE_DELAY,
                    "article.delay.publish.success.do",
                    articleId,
                    messagePostProcessor);
        } else if (article.getIsAppoint().equals(ArticleAppointType.IMMEDIATELY.type)) {
            article.setPublishTime(new Date());
        }

        int res = articleMapper.insert(article);
        if (res != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
        }
        updateArticleStatus(articleId, ArticleReviewStatus.WAITING_MANUAL.type);

//        // 通过阿里智能AI实现对文章文本检测(自动审核)
//        String reviewTextResult = aliTextReviewUtils.reviewTextContent(newArticleBO.getContent());
//
//        // 修改当前的文章状态 ---> 审核通过
//        if (ArticleReviewLevel.PASS.type.equalsIgnoreCase(reviewTextResult)) {
//            updateArticleStatus(id, ArticleReviewStatus.SUCCESS.type);
//        }
//        // 修改当前的文章状态 ---> 需要人工审核
//        else if (ArticleReviewLevel.REVIEW.type.equalsIgnoreCase(reviewTextResult)) {
//            updateArticleStatus(id, ArticleReviewStatus.WAITING_MANUAL.type);
//        }
//        // 修改当前的文章状态 ---> 审核不通过
//        else if (ArticleReviewLevel.BLOCK.type.equalsIgnoreCase(reviewTextResult)) {
//            updateArticleStatus(id, ArticleReviewStatus.FAILED.type);
//        }
    }

    @Transactional
    @Override
    public void timedPublishArticle() {
        articleMapper.timedPublishArticle();
    }


    @Transactional
    @Override
    public void timelyPublishArticle(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(ArticleAppointType.IMMEDIATELY.type);
        articleMapper.updateByPrimaryKeySelective(article);
    }

    @Override
    public PagedGridResult queryMyArticles(String userId,
                                           String keyword,
                                           Integer status,
                                           Date startDate,
                                           Date endDate,
                                           Integer page,
                                           Integer pageSize) {

        Example example = new Example(Article.class);
        example.orderBy("createTime").desc();
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("publishUserId", userId);
        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike("title", "%" + keyword + "%");
        }
        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }

        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        // 12 ---> REVIEWING + WAITING_MANUAL
        if (status != null && status == 12) {
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }
        if (startDate != null) {
            criteria.andGreaterThanOrEqualTo("publishTime", startDate);
        }
        if (endDate != null) {
            criteria.andLessThanOrEqualTo("publishTime", endDate);
        }
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, page);
    }

    @Transactional
    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", articleId);
        Article article = new Article();
        article.setArticleStatus(pendingStatus);

        int res = articleMapper.updateByExampleSelective(article, example);

        if (res != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

        // 如果审核通过 则查询article相关的字段并放入es中
        if (ArticleReviewStatus.SUCCESS.type.equals(pendingStatus)) {
            Article result = articleMapper.selectByPrimaryKey(articleId);
            if (ArticleAppointType.IMMEDIATELY.type.equals(result.getIsAppoint())) {
                String brief;
                String contentText = filterHTMLTag(result.getContent());
                if (contentText.length() <= 100) {
                    brief = contentText;
                } else {
                    brief = contentText.substring(0, 100);
                }
                ArticleEO articleEO = new ArticleEO();
                BeanUtils.copyProperties(result, articleEO);
                articleEO.setBrief(brief);
                IndexQuery indexQuery = new IndexQueryBuilder().withObject(articleEO).build();
                elasticsearchTemplate.index(indexQuery);
            }
        }
    }

    @Override
    public PagedGridResult queryAll(Integer status, Integer page, Integer pageSize, Integer deleteStatus) {
        Example example = new Example(Article.class);
        example.orderBy("createTime").desc();
        Example.Criteria criteria = example.createCriteria();

        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }
        // 12 ---> REVIEWING + WAITING_MANUAL
        if (status != null && status == 12) {
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }
        criteria.andEqualTo("isDelete", deleteStatus);

        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(example);
        return setterPagedGrid(list, page);
    }

    @Transactional
    @Override
    public void deleteArticle(String articleId) {
        articleMapper.deleteByPrimaryKey(articleId);
    }

    @Transactional
    @Override
    public void reReview(String articleId) {
        Example example = new Example(Article.class);
        Article article = new Article();
        article.setArticleStatus(ArticleReviewStatus.WAITING_MANUAL.type);
        articleMapper.updateByExampleSelective(article, example);
    }

    @Transactional
    @Override
    public void withdrawDelete(String articleId) {
        Example articleExample = new Example(Article.class);
        Article article = new Article();
        article.setId(articleId);
        article.setIsDelete(YesOrNo.NO.type);
        articleMapper.updateByExampleSelective(article, articleExample);
    }

    @Transactional
    @Override
    public void deleteArticle(String userId, String articleId) {
        Example articleExample = makeExampleCriteria(userId, articleId);

        Article pending = new Article();
        pending.setIsDelete(YesOrNo.YES.type);

        // 删除未审核过的文章 -----> 直接设置is_delete = 1
        int result = articleMapper.updateByExampleSelective(pending, articleExample);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
        }
        // 删除已通过审核的文章 还需删除gridFs和es中的数据
        Integer articleStatus = articleMapper.selectByPrimaryKey(articleId).getArticleStatus();
        if (ArticleReviewStatus.SUCCESS.type.equals(articleStatus)) {
            deleteArticleHtmlForGridFs(articleId);
            elasticsearchTemplate.delete(ArticleEO.class, articleId);
        }
    }

    private void deleteArticleHtmlForGridFs(String articleId) {
        // 查询文章的mongoId
        Article article = articleMapper.selectByPrimaryKey(articleId);
        String articleMongoId = article.getMongoFileId();

        // 删除GridFs上文章的html文件
        gridFSBucket.delete(new ObjectId(articleMongoId));

        // 删除消费端的文章html文件
        doDeleteArticleHtmlByMq(articleId);
    }

    private void doDeleteArticleHtmlByMq(String articleId) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_ARTICLE,
                "article.delete.do", articleId);
    }

    @Transactional
    @Override
    public void withdrawArticle(String userId, String articleId) {
        Example articleExample = makeExampleCriteria(userId, articleId);

        Article pending = new Article();
        pending.setArticleStatus(ArticleReviewStatus.WITHDRAW.type);

        int result = articleMapper.updateByExampleSelective(pending, articleExample);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
        }
        elasticsearchTemplate.delete(ArticleEO.class, articleId);

    }

    @Transactional
    @Override
    public void updateArticleToGridFS(String articleId, String articleMongoId) {
        Article article = new Article();
        article.setId(articleId);
        article.setMongoFileId(articleMongoId);
        articleMapper.updateByPrimaryKeySelective(article);

    }

    private Example makeExampleCriteria(String userId, String articleId) {
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = articleExample.createCriteria();
        criteria.andEqualTo("publishUserId", userId);
        criteria.andEqualTo("id", articleId);
        return articleExample;
    }

    private String filterHTMLTag(String html) {
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
}
