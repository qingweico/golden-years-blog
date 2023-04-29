package cn.qingweico.article.controller;

import cn.qingweico.core.base.BaseController;
import cn.qingweico.article.service.ArticleHistoryService;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.History;
import cn.qingweico.pojo.bo.DeleteArticleHistory;
import cn.qingweico.pojo.bo.IdBO;
import cn.qingweico.pojo.vo.ArticleHistoryVO;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.util.PagedResult;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
public class ArticleHistoryController extends BaseController {

    @Resource
    private ArticleHistoryService historyService;

    @Resource
    private ArticleController articleController;

    @PostMapping("inc")
    @ApiOperation(value = "用户浏览文章, 增加一条浏览记录", notes = "用户浏览文章, 增加一条浏览记录", httpMethod = "POST")
    public Result add(@RequestBody IdBO idBO) {
        String userId = idBO.getUserId();
        String articleId = idBO.getArticleId();
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(articleId)) {
            return Result.error();
        }
        historyService.addHistory(userId, articleId);
        return Result.ok();
    }


    @GetMapping("getUserHistory")
    @ApiOperation(value = "获取用户的浏览历史", notes = "获取用户的浏览历史", httpMethod = "GET")
    public Result getUserHistory(@RequestParam String userId, @RequestParam Integer pageNum,
                                 @RequestParam Integer pageSize) {
        checkPagingParams(pageNum, pageSize);
        PageInfo<History> pageInfo = historyService.getHistoryList(userId, pageNum, pageSize);
        List<ArticleHistoryVO> result = new ArrayList<>();
        for (History history : pageInfo.getList()) {
            String authorId = history.getUserId();
            String articleId = history.getArticleId();
            final Map<String, Object> authorInfo = articleController.geAuthorInfo(authorId);
            Article article = historyService.queryArticleById(articleId);
            ArticleHistoryVO articleHistoryVO = new ArticleHistoryVO();
            articleHistoryVO.setBrowseTime(history.getCreateTime());
            articleHistoryVO.setArticleName(article.getTitle());
            articleHistoryVO.setArticleId(articleId);
            articleHistoryVO.setUserId(authorId);
            articleHistoryVO.setArticleAuthorName((String) authorInfo.get(SysConst.AUTHOR_NAME));
            articleHistoryVO.setArticleAuthorFace((String) authorInfo.get(SysConst.AUTHOR_FACE));
            Integer readCounts = getCountsFromRedis(RedisConst.REDIS_ARTICLE_READ_COUNTS + SysConst.SYMBOL_COLON + articleId);
            Integer collectCounts = getCountsFromRedis(RedisConst.REDIS_ARTICLE_COLLECT_COUNTS + SysConst.SYMBOL_COLON + articleId);
            Integer starCounts = getCountsFromRedis(RedisConst.REDIS_ARTICLE_STAR_COUNTS + SysConst.SYMBOL_COLON + articleId);
            Integer commentCounts = getCountsFromRedis(RedisConst.REDIS_ARTICLE_COMMENT_COUNTS + SysConst.SYMBOL_COLON + articleId);
            articleHistoryVO.setArticleReadCounts(readCounts);
            articleHistoryVO.setArticleCollectCounts(collectCounts);
            articleHistoryVO.setArticleStarCounts(starCounts);
            articleHistoryVO.setArticleCommentCounts(commentCounts);
            result.add(articleHistoryVO);
        }
        PagedResult pgr = new PagedResult();
        pgr.setRows(result);
        pgr.setCurrentPage(pageInfo.getPageNum());
        pgr.setRecords(pageInfo.getTotal());
        pgr.setTotalPage(pageInfo.getPages());
        return Result.ok(pgr);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除用户的浏览历史", notes = "删除用户的浏览历史", httpMethod = "POST")
    public Result delete(@RequestBody DeleteArticleHistory history) {
        String userId = history.getUserId();
        if (StringUtils.isBlank(userId)) {
            return Result.error();
        }
        Integer deleteModel = history.getDeleteModel();
        historyService.deleteHistory(userId, deleteModel);
        return Result.r(Response.CLEAN_HISTORY_SUCCESS);
    }
}
