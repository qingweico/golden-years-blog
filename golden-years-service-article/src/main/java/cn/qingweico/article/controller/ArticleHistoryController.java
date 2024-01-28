package cn.qingweico.article.controller;

import cn.qingweico.article.service.ArticleHistoryService;
import cn.qingweico.core.base.BaseController;
import cn.qingweico.entity.Article;
import cn.qingweico.entity.History;
import cn.qingweico.entity.model.ArticleHistory;
import cn.qingweico.entity.model.ArticleRelated;
import cn.qingweico.entity.model.DeleteArticleHistory;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.result.Result;
import cn.qingweico.util.PagedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zqw
 * @date 2022/5/6
 */
@Slf4j
@Api(value = "文章浏览历史的接口定义", tags = {"文章浏览历史的接口定义"})
@RequestMapping("/article/history")
@RestController
public class ArticleHistoryController extends BaseController {

    @Resource
    private ArticleHistoryService historyService;

    @Resource
    private ArticleController articleController;

    @PostMapping("inc")
    @ApiOperation(value = "用户浏览文章, 增加一条浏览记录", notes = "用户浏览文章, 增加一条浏览记录", httpMethod = "POST")
    public Result add(@RequestBody @Valid ArticleRelated articleRelated) {
        String userId = articleRelated.getUserId();
        String articleId = articleRelated.getArticleId();
        historyService.addHistory(userId, articleId);
        return Result.ok();
    }


    @GetMapping("getUserHistory")
    @ApiOperation(value = "获取用户的浏览历史", notes = "获取用户的浏览历史", httpMethod = "GET")
    public Result getUserHistory(@RequestParam String userId, @RequestParam Integer pageNum,
                                 @RequestParam Integer pageSize) {
        checkPagingParams(pageNum, pageSize);
        PagedResult histories = historyService.getHistoryList(userId, pageNum, pageSize);
        // TODO 按照时间线 滚动 倒叙
        return Result.ok(histories);
    }

    @PostMapping("delete")
    @ApiOperation(value = "用户删除浏览历史", notes = "用户删除浏览历史", httpMethod = "POST")
    public Result delete(@RequestBody @Valid DeleteArticleHistory history) {
        String userId = history.getUserId();
        Integer deleteModel = history.getDeleteModel();
        historyService.deleteHistory(userId, deleteModel);
        return Result.r(Response.CLEAN_HISTORY_SUCCESS);
    }
}
