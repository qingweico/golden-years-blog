package cn.qingweico.article.restapi;

import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.service.ArticleHistoryService;
import cn.qingweico.global.RedisConf;
import cn.qingweico.global.SysConf;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.History;
import cn.qingweico.pojo.bo.DeleteArticleHistory;
import cn.qingweico.pojo.bo.IdBO;
import cn.qingweico.pojo.vo.ArticleHistoryVO;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zqw
 * @date 2022/5/6
 */
@Slf4j
@Api(value = "文章浏览历史相关的接口定义", tags = {"文章浏览历史相关的接口定义"})
@RequestMapping("/article/history")
@RestController
public class ArticleHistoryRestApi extends BaseRestApi {

    @Resource
    private ArticleHistoryService historyService;

    @Resource
    private ArticleRestApi articleRestApi;

    @PostMapping("inc")
    @ApiOperation(value = "用户浏览文章, 增加一条浏览记录", notes = "用户浏览文章, 增加一条浏览记录", httpMethod = "POST")
    public GraceJsonResult add(@RequestBody IdBO idBO) {
        String userId = idBO.getUserId();
        String articleId = idBO.getArticleId();
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(articleId)) {
            return GraceJsonResult.error();
        }
        historyService.addHistory(userId, articleId);
        return GraceJsonResult.ok();
    }


    @GetMapping("getUserHistory")
    @ApiOperation(value = "获取用户的浏览历史", notes = "获取用户的浏览历史", httpMethod = "GET")
    public GraceJsonResult getUserHistory(@RequestParam String userId, @RequestParam Integer pageNum,
                                          @RequestParam Integer pageSize) {
        checkPagingParams(pageNum, pageSize);
        PageInfo<History> pageInfo = historyService.getHistoryList(userId, pageNum, pageSize);
        List<ArticleHistoryVO> result = new ArrayList<>();
        for (History history : pageInfo.getList()) {
            String authorId = history.getUserId();
            String articleId = history.getArticleId();
            final Map<String, Object> authorInfo = articleRestApi.geAuthorInfo(authorId);
            Article article = historyService.queryArticleById(articleId);
            ArticleHistoryVO articleHistoryVO = new ArticleHistoryVO();
            articleHistoryVO.setBrowseTime(history.getCreateTime());
            articleHistoryVO.setArticleName(article.getTitle());
            articleHistoryVO.setArticleId(articleId);
            articleHistoryVO.setUserId(authorId);
            articleHistoryVO.setArticleAuthorName((String) authorInfo.get(SysConf.AUTHOR_NAME));
            articleHistoryVO.setArticleAuthorFace((String) authorInfo.get(SysConf.AUTHOR_FACE));
            Integer readCounts = getCountsFromRedis(RedisConf.REDIS_ARTICLE_READ_COUNTS + SysConf.SYMBOL_COLON + articleId);
            Integer collectCounts = getCountsFromRedis(RedisConf.REDIS_ARTICLE_COLLECT_COUNTS + SysConf.SYMBOL_COLON + articleId);
            Integer starCounts = getCountsFromRedis(RedisConf.REDIS_ARTICLE_STAR_COUNTS + SysConf.SYMBOL_COLON + articleId);
            Integer commentCounts = getCountsFromRedis(RedisConf.REDIS_ARTICLE_COMMENT_COUNTS + SysConf.SYMBOL_COLON + articleId);
            articleHistoryVO.setArticleReadCounts(readCounts);
            articleHistoryVO.setArticleCollectCounts(collectCounts);
            articleHistoryVO.setArticleStarCounts(starCounts);
            articleHistoryVO.setArticleCommentCounts(commentCounts);
            result.add(articleHistoryVO);
        }
        PagedGridResult pgr = new PagedGridResult();
        pgr.setRows(result);
        pgr.setCurrentPage(pageInfo.getPageNum());
        pgr.setRecords(pageInfo.getTotal());
        pgr.setTotalPage(pageInfo.getPages());
        return GraceJsonResult.ok(pgr);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除用户的浏览历史", notes = "删除用户的浏览历史", httpMethod = "POST")
    public GraceJsonResult delete(@RequestBody DeleteArticleHistory history) {
        String userId = history.getUserId();
        if (StringUtils.isBlank(userId)) {
            return GraceJsonResult.error();
        }
        Integer deleteModel = history.getDeleteModel();
        historyService.deleteHistory(userId, deleteModel);
        return new GraceJsonResult(ResponseStatusEnum.CLEAN_HISTORY_SUCCESS);
    }
}
