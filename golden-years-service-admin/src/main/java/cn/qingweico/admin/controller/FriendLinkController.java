package cn.qingweico.admin.controller;

import cn.qingweico.admin.service.FriendLinkService;
import cn.qingweico.core.base.BaseController;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.pojo.bo.SaveFriendLinkBO;
import cn.qingweico.pojo.mo.FriendLink;
import cn.qingweico.util.PagedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Api(value = "首页友情链接", tags = {"首页友情链接"})
@RequestMapping("fl")
@RestController
public class FriendLinkController extends BaseController {

    @Resource
    private FriendLinkService friendLinkService;

    @ApiOperation(value = "新增或者修改友情链接", notes = "新增或者修改友情链接", httpMethod = "POST")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdateFriendLink(@RequestBody @Valid SaveFriendLinkBO saveFriendLinkBO) {
        FriendLink friendLink = new FriendLink();
        String pendingFriendLinkId = saveFriendLinkBO.getId();
        BeanUtils.copyProperties(saveFriendLinkBO, friendLink);
        if (StringUtils.isEmpty(pendingFriendLinkId)) {
            friendLink.setCreateTime(new Date());
        }
        friendLink.setUpdateTime(new Date());
        friendLinkService.saveOrUpdateFriendLink(friendLink);
        if (StringUtils.isEmpty(pendingFriendLinkId)) {
            return Result.r(Response.APPEND_SUCCESS);
        } else {
            return Result.r(Response.ALERT_SUCCESS);
        }
    }

    @ApiOperation(value = "管理员查询友情链接列表", notes = "管理员查询友情链接列表", httpMethod = "GET")
    @GetMapping("/list")
    public Result getFriendLinkList(@RequestParam String linkName,
                                    @RequestParam Integer isDelete,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        PagedResult result = friendLinkService.getFriendLinkList(linkName, isDelete, page, pageSize);
        return Result.ok(result);
    }

    @ApiOperation(value = "批量删除友情链接", notes = "批量删除友情链接", httpMethod = "POST")
    @PostMapping("/delete")
    public Result batchDelete(@RequestBody List<String> ids) {
        friendLinkService.deleteAll(ids);
        return Result.r(Response.DELETE_SUCCESS);
    }

    @ApiOperation(value = "删除友情链接", notes = "删除友情链接", httpMethod = "POST")
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") String linkId) {
        friendLinkService.delete(linkId);
        return Result.r(Response.DELETE_SUCCESS);
    }

    @ApiOperation(value = "首页友情链接列表", notes = "查询友情链接列表", httpMethod = "GET")
    @GetMapping("/portal/list")
    public Result getIndexFriendLinkList() {
        return Result.ok(friendLinkService.queryIndexFriendLinkList(YesOrNo.NO.type));
    }
}
