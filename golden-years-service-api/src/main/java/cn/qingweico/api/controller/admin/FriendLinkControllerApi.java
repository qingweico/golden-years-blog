package cn.qingweico.api.controller.admin;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.pojo.bo.SaveFriendLinkBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author zqw
 * @date 2021/9/9
 */
@Api(value = "首页友情链接", tags = {"首页友情链接"})
@RequestMapping("fl")
public interface FriendLinkControllerApi {
   /**
    * 新增或者修改友情链接
    * @param saveFriendLinkBO SaveFriendLinkBO
    * @return GraceJsonResult
    */
   @ApiOperation(value = "新增或者修改友情链接", notes = "新增或者修改友情链接", httpMethod = "POST")
   @PostMapping("/saveOrUpdate")
   GraceJsonResult saveOrUpdateFriendLink(@RequestBody @Valid SaveFriendLinkBO saveFriendLinkBO);

   /**
    * 查询友情链接列表
    * @return GraceJsonResult
    */
   @ApiOperation(value = "查询友情链接列表", notes = "查询友情链接列表", httpMethod = "GET")
   @GetMapping("/list")
   GraceJsonResult getFriendLinkList();

   /**
    * 删除友情链接
    * @param linkId 友情链接id
    * @return GraceJsonResult
    */
   @ApiOperation(value = "删除友情链接", notes = "删除友情链接", httpMethod = "POST")
   @PostMapping("/delete")
   GraceJsonResult delete(String linkId);

   /**
    * 首页友情链接列表
    * @return GraceJsonResult
    */
   @ApiOperation(value = "首页友情链接列表", notes = "查询友情链接列表", httpMethod = "GET")
   @GetMapping("/portal/list")
   GraceJsonResult getIndexFriendLinkList();

}
