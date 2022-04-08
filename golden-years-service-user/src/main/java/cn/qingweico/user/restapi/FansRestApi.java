package cn.qingweico.user.restapi;

import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.enums.Sex;
import cn.qingweico.pojo.bo.FansBO;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.vo.FansCountsVO;
import cn.qingweico.user.service.FanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * @author zqw
 * @date 2021/9/12
 */
@Api(value = "粉丝管理", tags = {"粉丝管理"})
@RequestMapping("fans")
@RestController
public class FansRestApi extends BaseRestApi {

    @Resource
    private FanService fanService;

    @ApiOperation(value = "查询当前用户是否关注作者", notes = "查询当前用户是否关注作者", httpMethod = "GET")
    @GetMapping("/hasFollowThisAuthorOrNot")
    public GraceJsonResult hasFollowThisAuthorOrNot(@RequestParam String userId,
                                                    @RequestParam String fanId) {
        boolean res = fanService.isMeFollowThisAuthor(userId, fanId);
        return GraceJsonResult.ok(res);
    }

    @ApiOperation(value = "用户关注作者, 成为粉丝", notes = "用户关注作者, 成为粉丝", httpMethod = "POST")
    @PostMapping("/follow")
    public GraceJsonResult follow(@RequestBody FansBO fansBO) {
        String authorId = fansBO.getAuthorId();
        String fanId = fansBO.getFanId();
        if (Objects.equals(authorId, "") || Objects.equals(fanId, "")) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
        if (authorId.equals(fanId)) {
            return new GraceJsonResult(ResponseStatusEnum.CANNOT_FOLLOW_ONESELF);
        }
        fanService.follow(authorId, fanId);
        return new GraceJsonResult(ResponseStatusEnum.FOLLOWED);
    }

    @ApiOperation(value = "用户取关作者", notes = "用户取关作者", httpMethod = "POST")
    @PostMapping("/unfollow")
    public GraceJsonResult unfollow(@RequestBody FansBO fansBO) {
        String authorId = fansBO.getAuthorId();
        String fanId = fansBO.getFanId();
        fanService.unfollow(authorId, fanId);
        return new GraceJsonResult(ResponseStatusEnum.UNFOLLOWED);
    }

    @ApiOperation(value = "查询我的粉丝列表", notes = "查询我的粉丝列表", httpMethod = "GET")
    @GetMapping("/query")
    public GraceJsonResult query(@RequestParam String userId,
                                 @RequestParam Integer page,
                                 @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        return GraceJsonResult.ok(fanService.getMyFansList(userId, page, pageSize));
    }

    @ApiOperation(value = "查询我的粉丝列表(es)", notes = "查询我的粉丝列表(es)", httpMethod = "GET")
    @GetMapping("es/query")
    public GraceJsonResult queryViaEs(@RequestParam String userId,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        return GraceJsonResult.ok(fanService.getMyFansListViaEs(userId, page, pageSize));
    }

    @ApiOperation(value = "查询男女粉丝的数量", notes = "查询男女粉丝的数量", httpMethod = "GET")
    @GetMapping("/queryRatio")
    public GraceJsonResult queryRatio(@RequestParam String userId) {
        int manCounts = fanService.queryFansCounts(userId, Sex.MAN);
        int womanCounts = fanService.queryFansCounts(userId, Sex.WOMAN);
        FansCountsVO fanCountsVO = new FansCountsVO();
        fanCountsVO.setManCounts(manCounts);
        fanCountsVO.setWomanCounts(womanCounts);
        return GraceJsonResult.ok(fanCountsVO);
    }

    @ApiOperation(value = "查询男女粉丝的数量(es)", notes = "查询男女粉丝的数量(es)", httpMethod = "GET")
    @GetMapping("es/queryRatio")
    public GraceJsonResult queryRatioViaEs(@RequestParam String userId) {
        return GraceJsonResult.ok(fanService.queryFansCountsViaEs(userId));
    }

    @ApiOperation(value = "根据地域分布查询粉丝的数量", notes = "根据地域分布查询粉丝的数量", httpMethod = "GET")
    @GetMapping("/queryRatioByRegion")
    public GraceJsonResult queryRatioByRegion(@RequestParam String userId) {
        return GraceJsonResult.ok(fanService.queryRatioByRegion(userId));
    }

    @ApiOperation(value = "根据地域分布查询粉丝的数量(es)", notes = "根据地域分布查询粉丝的数量(es)", httpMethod = "GET")
    @GetMapping("es/queryRatioByRegion")
    public GraceJsonResult queryRatioByRegionViaEs(@RequestParam String userId) {
        return GraceJsonResult.ok(fanService.queryRatioByRegionViaEs(userId));
    }

    @ApiOperation(value = "被动更新粉丝信息", notes = "被动更新粉丝信息", httpMethod = "POST")
    @PostMapping("/passive")
    public GraceJsonResult passive(@RequestParam String relationId,
                                   @RequestParam String fanId) {
        fanService.passive(relationId, fanId);
        return GraceJsonResult.ok();
    }
}
