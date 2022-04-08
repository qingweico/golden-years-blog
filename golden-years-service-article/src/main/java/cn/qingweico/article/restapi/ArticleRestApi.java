package cn.qingweico.article.restapi;

import cn.qingweico.api.config.RabbitMqConfig;
import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.article.clients.ArticleDetailClient;
import cn.qingweico.article.service.ArticleService;
import cn.qingweico.enums.ArticleCoverType;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.global.RedisConf;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.bo.NewArticleBO;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedGridResult;
import com.mongodb.client.gridfs.GridFSBucket;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author zqw
 * @date 2021/9/11
 */
@Slf4j
@RestController
@Api(value = "文章发布相关的接口定义", tags = {"文章发布相关的接口定义"})
@RequestMapping("article")
public class ArticleRestApi extends BaseRestApi {


    @Resource
    private ArticleService articleService;

    @Resource
    private GridFSBucket gridFsBucket;

    @ApiOperation(value = "发布新文章或者修改文章", notes = "发布新文章或者修改文章", httpMethod = "POST")
    @PostMapping("/user/publishOrUpdate")
    public GraceJsonResult createArticle(@RequestBody @Valid NewArticleBO newArticleBO) {
        String articleId = newArticleBO.getArticleId();
        // 添加文章
        if(StringUtils.isBlank(articleId)) {
            if (newArticleBO.getArticleType().equals(ArticleCoverType.ONE_IMAGE.type)) {
                if (StringUtils.isBlank(newArticleBO.getArticleCover())) {
                    return GraceJsonResult.errorCustom(ResponseStatusEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
                }
            } else if (newArticleBO.getArticleType().equals(ArticleCoverType.WORDS.type)) {
                newArticleBO.setArticleCover("");
            }

            // 判断分类Id是否存在 (从缓存中取出数据)
            String categoryJson = redisOperator.get(RedisConf.REDIS_ARTICLE_CATEGORY);
            List<Category> categories = JsonUtils.jsonToList(categoryJson, Category.class);
            if (categories == null) {
                return GraceJsonResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
            }
            Category res = categories.stream()
                    .filter(category -> category.getId().equals(newArticleBO.getCategoryId()))
                    .collect(toList()).get(0);

            if (res == null) {
                return GraceJsonResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
            }
            articleService.createArticle(newArticleBO);
            return new GraceJsonResult(ResponseStatusEnum.ARTICLE_PUBLISH_SUCCESS);
        }else {
            articleService.updateArticle(newArticleBO);
            return new GraceJsonResult(ResponseStatusEnum.ARTICLE_ALTER_SUCCESS);
        }

    }

    @ApiOperation(value = "条件查询用户所有的文章", notes = "条件查询用户所有的文章", httpMethod = "GET")
    @GetMapping("/user/query")
    public GraceJsonResult queryUserArticles(@RequestParam String userId,
                                             @RequestParam String keyword,
                                             @RequestParam Integer categoryId,
                                             @RequestParam Integer status,
                                             @RequestParam Date startDate,
                                             @RequestParam Date endDate,
                                             @RequestParam Integer page,
                                             @RequestParam Integer pageSize) {


        if (StringUtils.isBlank(userId)) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }
        checkPagingParams(page, pageSize);

        PagedGridResult res = articleService.queryMyArticles(userId,
                keyword,
                categoryId,
                status,
                startDate,
                endDate,
                page,
                pageSize);
        return GraceJsonResult.ok(res);
    }

    @ApiOperation(value = "管理员查询用户的所有文章列表", notes = "管理员查询用户的所有文章列表", httpMethod = "GET")
    @GetMapping("/admin/query")
    public GraceJsonResult query(Integer status,
                                 Integer categoryId,
                                 Integer page,
                                 Integer pageSize,
                                 Integer deleteStatus) {
        checkPagingParams(page, pageSize);
        PagedGridResult res = articleService.query(status, categoryId, page, pageSize, deleteStatus);
        return GraceJsonResult.ok(res);
    }

    @ApiOperation(value = "管理员对文章进行审核", notes = "管理员对文章进行审核", httpMethod = "POST")
    @PostMapping("/admin/doReview")
    public GraceJsonResult doReview(String articleId, Integer passOrNot) {
        Integer pendingStatus;
        // 文章审核通过
        if (YesOrNo.YES.type.equals(passOrNot)) {
            pendingStatus = ArticleReviewStatus.SUCCESS.type;
        }
        // 文章审核不通过
        else if (YesOrNo.NO.type.equals(passOrNot)) {
            pendingStatus = ArticleReviewStatus.FAILED.type;
        } else {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
        articleService.updateArticleStatus(articleId, pendingStatus);


        // 审核成功 生成文章详情页静态html
//        if (ArticleReviewStatus.SUCCESS.type.equals(pendingStatus)) {
//
//            // 将生成的文章详情静态html上传至GridFS, 并返回articleMongoId
//            String articleMongoId = null;
//            try {
//                articleMongoId = createArticleHtmlToGridFs(articleId);
//            } catch (IOException | TemplateException e) {
//                e.printStackTrace();
//            }
//            // 关联文章与GridFS中的静态文件
//            articleService.updateArticleToGridFs(articleId, articleMongoId);
//
//            // 发送消息到mq队列, 让监听mq队列的消费者下载html
//            doDownloadArticleHtmlByMq(articleId, articleMongoId);
//
//        }

        if (YesOrNo.YES.type.equals(passOrNot)) {
            return new GraceJsonResult(ResponseStatusEnum.ARTICLE_REVIEW_PASS);
        } else {
            return new GraceJsonResult(ResponseStatusEnum.ARTICLE_REVIEW_FAIL);
        }
    }

    @ApiOperation(value = "重新对文章进行审核", notes = "重新对文章进行审核", httpMethod = "POST")
    @PostMapping("/admin/reReview")
    public GraceJsonResult reReview(String articleId) {
        articleService.reReview(articleId);
        return new GraceJsonResult(ResponseStatusEnum.ARTICLE_RE_REVIEW_PASS);
    }

    @ApiOperation(value = "管理员对文章进行删除(delete sql)", notes = "管理员对文章进行删除(delete sql)", httpMethod = "POST")
    @PostMapping("/admin/delete")
    public GraceJsonResult delete(String articleId) {
        articleService.deleteArticle(articleId);
        return new GraceJsonResult(ResponseStatusEnum.DELETE_SUCCESS);
    }

    @ApiOperation(value = "管理员撤回用户对文章的删除", notes = "管理员撤回用户对文章的删除", httpMethod = "POST")
    @PostMapping("/admin/withdrawDelete")
    public GraceJsonResult withdrawDelete(String articleId) {
        articleService.withdrawDelete(articleId);
        return new GraceJsonResult(ResponseStatusEnum.DELETE_HAS_WITHDRAW);
    }

    @Resource
    private RabbitTemplate rabbitTemplate;

    private void doDownloadArticleHtmlByMq(String articleId, String articleMongoId) {

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_ARTICLE,
                "article.download.do",
                articleId + "," + articleMongoId);
    }

    @ApiOperation(value = "用户删除文章", notes = "用户删除文章", httpMethod = "POST")
    @PostMapping("/user/delete")
    public GraceJsonResult delete(String userId, String articleId) {
        articleService.deleteArticle(userId, articleId);
        return new GraceJsonResult(ResponseStatusEnum.ARTICLE_DELETE_SUCCESS);

    }

    @ApiOperation(value = "用户撤回文章", notes = "用户撤回文章", httpMethod = "POST")
    @PostMapping("/user/withdraw")
    public GraceJsonResult withdraw(String userId, String articleId) {
        articleService.withdrawArticle(userId, articleId);
        return new GraceJsonResult(ResponseStatusEnum.ARTICLE_WITHDRAW_SUCCESS);
    }


    private String createArticleHtmlToGridFs(String articleId) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.getVersion());
        String classpath = Objects.requireNonNull(this.getClass().getResource("/")).getPath();
        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        Template template = cfg.getTemplate("detail.ftl", "utf-8");

        // 获得文章的详情数据
        GraceJsonResult result = getArticleDetail(articleId);
        if (!result.getSuccess()) {
            log.error("文章详情获取失败");
            return "";
        }
        String detailVoJson = JsonUtils.objectToJson(result.getData());
        ArticleDetailVO detailVO = JsonUtils.jsonToPojo(detailVoJson, ArticleDetailVO.class);
        Map<String, Object> map = new HashMap<>(1);
        map.put("articleDetail", detailVO);

        String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        InputStream inputStream = IOUtils.toInputStream(htmlContent);
        assert detailVO != null;
        ObjectId fileId = gridFsBucket.uploadFromStream(detailVO.getId() + ".html", inputStream);
        return fileId.toString();
    }

    @Resource
    private ArticleDetailClient articleDetailClient;

    public GraceJsonResult getArticleDetail(String articleId) {
        return articleDetailClient.getArticleDetail(articleId);
    }
}
