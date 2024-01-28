package cn.qingweico.user.controller;

import cn.qingweico.core.base.BaseController;
import cn.qingweico.enums.Sex;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.result.Result;
import cn.qingweico.user.entity.FansBO;
import cn.qingweico.user.service.FanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @author zqw
 * @date 2021/9/12
 */
@Api(value = "粉丝管理", tags = {"粉丝管理"})
@RequestMapping("fans")
@RestController
public class FansController extends BaseController {

    @Resource
    private FanService fanService;

    @ApiOperation(value = "查询当前用户是否关注作者", notes = "查询当前用户是否关注作者", httpMethod = "GET")
    @GetMapping("/hasFollowThisAuthorOrNot")
    public Result hasFollowThisAuthorOrNot(@RequestParam String userId, @RequestParam String fanId) {
        boolean res = fanService.isMeFollowThisAuthor(userId, fanId);
        return Result.ok(res);
    }

    @ApiOperation(value = "用户关注作者, 成为粉丝", notes = "用户关注作者, 成为粉丝", httpMethod = "POST")
    @PostMapping("/follow")
    public Result follow(@RequestBody FansBO fansBO) {
        String authorId = fansBO.getAuthorId();
        String fanId = fansBO.getFanId();
        if (Objects.equals(authorId, SysConst.EMPTY_STRING) || Objects.equals(fanId, SysConst.EMPTY_STRING)) {
            return Result.r(Response.SYSTEM_OPERATION_ERROR);
        }
        if (authorId.equals(fanId)) {
            return Result.r(Response.CANNOT_FOLLOW_ONESELF);
        }
        fanService.follow(authorId, fanId);
        return Result.r(Response.FOLLOWED);
    }

    @ApiOperation(value = "用户取关作者", notes = "用户取关作者", httpMethod = "POST")
    @PostMapping("/unfollow")
    public Result unfollow(@RequestBody FansBO fansBO) {
        String authorId = fansBO.getAuthorId();
        String fanId = fansBO.getFanId();
        fanService.unfollow(authorId, fanId);
        return Result.r(Response.UNFOLLOWED);
    }

    @ApiOperation(value = "查询我的粉丝列表", notes = "查询我的粉丝列表", httpMethod = "GET")
    @GetMapping("/query")
    public Result query(@RequestParam String userId, @RequestParam Integer page, @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        return Result.ok(fanService.getMyFansList(userId, page, pageSize));
    }


    @ApiOperation(value = "查询男女粉丝的数量", notes = "查询男女粉丝的数量", httpMethod = "GET")
    @GetMapping("/queryRatio")
    public Result queryRatio(@RequestParam String userId) {
        int manCounts = fanService.queryFansCounts(userId, Sex.MAN);
        int womanCounts = fanService.queryFansCounts(userId, Sex.WOMAN);
        Map<String, Object> result = new HashMap<>();
        result.put("man", manCounts);
        result.put("woman", womanCounts);
        return Result.ok(result);
    }

    @ApiOperation(value = "根据地域分布查询粉丝的数量", notes = "根据地域分布查询粉丝的数量", httpMethod = "GET")
    @GetMapping("/queryRatioByRegion")
    public Result queryRatioByRegion(@RequestParam String userId) {
        return Result.ok(fanService.queryRatioByRegion(userId));
    }
}
