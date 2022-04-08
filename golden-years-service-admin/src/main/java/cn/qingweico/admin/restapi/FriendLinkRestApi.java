package cn.qingweico.admin.restapi;

import cn.qingweico.admin.service.FriendLinkService;
import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.bo.SaveFriendLinkBO;
import cn.qingweico.pojo.mo.FriendLink;
import cn.qingweico.util.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Api(value = "首页友情链接", tags = {"首页友情链接"})
@RequestMapping("fl")
@RestController
public class FriendLinkRestApi extends BaseRestApi {

    @Resource
    private FriendLinkService friendLinkService;

    @ApiOperation(value = "新增或者修改友情链接", notes = "新增或者修改友情链接", httpMethod = "POST")
    @PostMapping("/saveOrUpdate")
    public GraceJsonResult saveOrUpdateFriendLink(@RequestBody @Valid SaveFriendLinkBO saveFriendLinkBO) {
        FriendLink friendLink = new FriendLink();
        String pendingFriendLinkId = saveFriendLinkBO.getId();
        BeanUtils.copyProperties(saveFriendLinkBO, friendLink);
        if (StringUtils.isEmpty(pendingFriendLinkId)) {
            friendLink.setCreateTime(new Date());
        }
        friendLink.setUpdateTime(new Date());
        friendLinkService.saveOrUpdateFriendLink(friendLink);
        if (StringUtils.isEmpty(pendingFriendLinkId)) {
            return new GraceJsonResult(ResponseStatusEnum.APPEND_SUCCESS);
        } else {
            return new GraceJsonResult(ResponseStatusEnum.ALERT_SUCCESS);
        }
    }

    @ApiOperation(value = "管理员查询友情链接列表", notes = "管理员查询友情链接列表", httpMethod = "GET")
    @GetMapping("/list")
    public GraceJsonResult getFriendLinkList(@RequestParam Integer page,
                                             @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedGridResult result = friendLinkService.getFriendLinkList(page, pageSize);
        return GraceJsonResult.ok(result);
    }

    @ApiOperation(value = "删除友情链接", notes = "删除友情链接", httpMethod = "POST")
    @PostMapping("/delete/{id}")
    public GraceJsonResult delete(@PathVariable("id") String linkId) {
        friendLinkService.delete(linkId);
        return new GraceJsonResult(ResponseStatusEnum.DELETE_SUCCESS);
    }

    @ApiOperation(value = "首页友情链接列表", notes = "查询友情链接列表", httpMethod = "GET")
    @GetMapping("/portal/list")
    public GraceJsonResult getIndexFriendLinkList() {
        return GraceJsonResult.ok(friendLinkService.queryAllFriendLinkList(YesOrNo.NO.type));
    }
}