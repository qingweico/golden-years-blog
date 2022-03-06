package cn.qingweico.api.controller.user;

import cn.qingweico.result.GraceJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
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
     * @param userId 作者id
     * @param fanId 粉丝id
     * @return GraceJsonResult
     */
    @ApiOperation(value = "用户关注作者, 成为粉丝", notes = "用户关注作者, 成为粉丝", httpMethod = "POST")
    @PostMapping("/follow")
    GraceJsonResult follow(@RequestParam String userId,
                           @RequestParam String fanId);


    /**
     * 用户取关作者
     * @param userId 作者id
     * @param fanId 粉丝id
     * @return GraceJsonResult
     */
    @ApiOperation(value = "用户取关作者", notes = "用户取关作者", httpMethod = "POST")
    @PostMapping("/unfollow")
    GraceJsonResult unfollow(@RequestParam String userId,
                             @RequestParam String fanId);


    @ApiOperation(value = "查询我的粉丝列表", notes = "查询我的粉丝列表", httpMethod = "GET")
    @GetMapping("/query")
    GraceJsonResult query(@RequestParam String userId,
                          @RequestParam Integer page,
                          @RequestParam Integer pageSize);
    @ApiOperation(value = "查询我的粉丝列表(es)", notes = "查询我的粉丝列表(es)", httpMethod = "GET")
    @GetMapping("es/query")
    GraceJsonResult queryViaEs(@RequestParam String userId,
                          @RequestParam Integer page,
                          @RequestParam Integer pageSize);

    @ApiOperation(value = "查询男女粉丝的数量", notes = "查询男女粉丝的数量", httpMethod = "GET")
    @GetMapping("/queryRatio")
    GraceJsonResult queryRatio(@RequestParam String userId);

    @ApiOperation(value = "查询男女粉丝的数量(es)", notes = "查询男女粉丝的数量(es)", httpMethod = "GET")
    @GetMapping("es/queryRatio")
    GraceJsonResult queryRatioViaEs(@RequestParam String userId);


    @ApiOperation(value = "根据地域分布查询粉丝的数量", notes = "根据地域分布查询粉丝的数量", httpMethod = "GET")
    @GetMapping("/queryRatioByRegion")
    GraceJsonResult queryRatioByRegion(@RequestParam String userId);

    @ApiOperation(value = "根据地域分布查询粉丝的数量(es)", notes = "根据地域分布查询粉丝的数量(es)", httpMethod = "GET")
    @GetMapping("es/queryRatioByRegion")
    GraceJsonResult queryRatioByRegionViaEs(@RequestParam String userId);



    @ApiOperation(value = "被动更新粉丝信息", notes = "被动更新粉丝信息", httpMethod = "POST")
    @PostMapping("/passive")
    GraceJsonResult passive(@RequestParam String relationId,
                            @RequestParam String fanId);
}
