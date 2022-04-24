package cn.qingweico.admin.restapi;

import cn.qingweico.admin.clients.ArticleClient;
import cn.qingweico.admin.clients.UserClient;
import cn.qingweico.admin.service.WebVisitService;
import cn.qingweico.global.SysConf;
import cn.qingweico.result.GraceJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页RestApi
 *
 * @author zqw
 * @date 2022/4/18
 */
@RestController
@RequestMapping("/index")
@Api(value = "首页相关接口", tags = {"首页相关接口"})
@Slf4j
public class IndexRestApi {
    @Resource
    private WebVisitService webVisitService;
    @Resource
    private ArticleClient articleClient;
    @Resource
    private UserClient userClient;

    @ApiOperation(value = "首页初始化数据", notes = "首页初始化数据", response = String.class)
    @GetMapping(value = "/init")
    public GraceJsonResult init() {
        Map<String, Object> map = new HashMap<>(SysConf.NUM_FOUR);
        map.put(SysConf.BLOG_COUNT, articleClient.getArticleCounts());
        map.put(SysConf.COMMENT_COUNT, articleClient.getCommentCount());
        map.put(SysConf.USER_COUNT, userClient.getUserCounts());
        map.put(SysConf.VISIT_COUNT, webVisitService.getWebVisitCount());
        return GraceJsonResult.ok(map);
    }

    @ApiOperation(value = "获取最近一周用户独立IP数和访问量", notes = "获取最近一周用户独立IP数和访问量")
    @GetMapping(value = "/getVisitByWeek")
    public GraceJsonResult getVisitByWeek() {
        return GraceJsonResult.ok(webVisitService.getVisitByWeek());
    }

    @ApiOperation(value = "获取每个标签下文章数目", notes = "获取每个标签下文章数目")
    @GetMapping(value = "/getBlogCountByTag")
    public GraceJsonResult getBlogCountByTag() {
        return articleClient.getBlogCountByTag();
    }

    @ApiOperation(value = "获取每个分类下文章数目", notes = "获取每个分类下文章数目")
    @GetMapping(value = "/getBlogCountByCategory")
    public GraceJsonResult getBlogCountByBlogSort() {
        return articleClient.getCategoryListWithArticleCount();
    }

    @ApiOperation(value = "获取一年内的文章贡献数", notes = "获取一年内的文章贡献度")
    @GetMapping(value = "/getBlogContributeCount")
    public GraceJsonResult getBlogContributeCount() {
        return articleClient.getBlogContributeCount();
    }
}
