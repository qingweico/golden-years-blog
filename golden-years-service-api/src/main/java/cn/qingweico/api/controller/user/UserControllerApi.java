package cn.qingweico.api.controller.user;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.pojo.bo.UpdateUserInfoBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author:qiming
 * @date: 2021/9/5
 */
@Api(value = "用户信息相关的接口定义", tags = {"用户信息相关的接口定义"})
@RequestMapping("user")
public interface UserControllerApi {

   @ApiOperation(value = "获得用户账户信息", notes = "获得用户账户信息", httpMethod = "POST")
   @PostMapping("/getAccountInfo")
   GraceJsonResult getAccountInfo(String userId);


   @ApiOperation(value = "获得用户基本信息", notes = "获得用户基本信息", httpMethod = "POST")
   @PostMapping("/getUserBasicInfo")
   GraceJsonResult getUserBasicInfo(String userId);

   @ApiOperation(value = "修改/完善用户信息", notes = "修改/完善用户信息", httpMethod = "POST")
   @PostMapping("/updateUserInfo")
   GraceJsonResult updateUserInfo(@RequestBody @Valid UpdateUserInfoBO updateUserInfoBO);

   @ApiOperation(value = "根据用户ids查询用户信息", notes = "根据用户ids查询用户信息", httpMethod = "GET")
   @GetMapping("/queryByIds")
   GraceJsonResult queryByIds(@RequestParam String userIds);

   @ApiOperation(value = "查询用户的登陆日志列表", notes = "查询用户的登陆日志列表", httpMethod = "POST")
   @PostMapping("/getLoginLogList")
   GraceJsonResult getLoginLogList(String userId, Integer page, Integer pageSize);
}
