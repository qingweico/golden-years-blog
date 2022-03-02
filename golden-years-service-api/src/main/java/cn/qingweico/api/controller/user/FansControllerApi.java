package cn.qingweico.api.controller.user;

import cn.qingweico.result.GraceJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zqw
 * @date 2021/9/12
 */
@Api(value = "粉丝管理", tags = {"粉丝管理"})
@RequestMapping("fans")
public interface FansControllerApi {

    /**
     * 查询当前用户是否关注作者
     * @param authorId 作者id
     * @param fanId 粉丝id
     * @return  已关注或者未关注 {@code true or false}
     */
    @ApiOperation(value = "查询当前用户是否关注作者", notes = "查询当前用户是否关注作者", httpMethod = "POST")
    @PostMapping("/isMeFollowThisAuthor")
    GraceJsonResult isMeFollowThisAuthor(@RequestParam String authorId,
                                         @RequestParam String fanId);

    /**
     * 用户关注作家, 成为粉丝
     * @param authorId 作者id
     * @param fanId 粉丝id
     * @return GraceJsonResult
     */
    @ApiOperation(value = "用户关注作家, 成为粉丝", notes = "用户关注作家, 成为粉丝", httpMethod = "POST")
    @PostMapping("/follow")
    GraceJsonResult follow(@RequestParam String authorId,
                           @RequestParam String fanId);


    /**
     * 用户取关作家
     * @param authorId 作者id
     * @param fanId 粉丝id
     * @return GraceJsonResult
     */
    @ApiOperation(value = "用户取关作家", notes = "用户取关作家", httpMethod = "POST")
    @PostMapping("/unfollow")
    GraceJsonResult unfollow(@RequestParam String authorId,
                             @RequestParam String fanId);


    @ApiOperation(value = "查询我的粉丝列表", notes = "查询我的粉丝列表", httpMethod = "POST")
    @PostMapping("/queryAll")
    GraceJsonResult queryAll(@RequestParam String authorId,
                             @RequestParam Integer page,
                             @RequestParam Integer pageSize);

    @ApiOperation(value = "查询男女粉丝的数量", notes = "查询男女粉丝的数量", httpMethod = "POST")
    @PostMapping("/queryRatio")
    GraceJsonResult queryRatio(@RequestParam String authorId);

    @ApiOperation(value = "查询男女粉丝的数量(es)", notes = "查询男女粉丝的数量(es)", httpMethod = "POST")
    @PostMapping("es/queryRatio")
    GraceJsonResult queryRatioViaEs(@RequestParam String authorId);


    @ApiOperation(value = "根据地域分布查询粉丝的数量", notes = "根据地域分布查询粉丝的数量", httpMethod = "POST")
    @PostMapping("/queryRatioByRegion")
    GraceJsonResult queryRatioByRegion(@RequestParam String authorId);

    @ApiOperation(value = "根据地域分布查询粉丝的数量(es)", notes = "根据地域分布查询粉丝的数量(es)", httpMethod = "POST")
    @PostMapping("es/queryRatioByRegion")
    GraceJsonResult queryRatioByRegionViaEs(@RequestParam String authorId);



    @ApiOperation(value = "被动更新粉丝信息", notes = "被动更新粉丝信息", httpMethod = "POST")
    @PostMapping("/passive")
    GraceJsonResult passive(@RequestParam String relationId,
                            @RequestParam String fanId);
}
