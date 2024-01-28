package cn.qingweico.user.controller;

import cn.qingweico.entity.User;
import cn.qingweico.entity.model.*;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.global.SysConst;
import cn.qingweico.global.RedisConst;
import cn.qingweico.result.Response;
import cn.qingweico.core.base.BaseController;
import cn.qingweico.result.Result;
import cn.qingweico.user.service.LoginLogService;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

/**
 * @author zqw
 * @date 2021/9/11
 */
@Slf4j
@Api(value = "用户信息相关的接口定义", tags = {"用户信息相关的接口定义"})
@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private LoginLogService loginLogService;

    @ApiOperation(value = "查询网站的所有注册用户", notes = "查询网站的所有注册用户", httpMethod = "POST")
    @GetMapping("/query")
    public Result query(@RequestParam String nickname,
                        @RequestParam Integer status,
                        @RequestParam String mobile,
                        @RequestParam Date startDate,
                        @RequestParam Date endDate,
                        @RequestParam Integer page,
                        @RequestParam Integer pageSize) {

        checkPagingParams(page, pageSize);
        PagedResult pagedResult = userService.queryUserList(nickname,
                status,
                mobile,
                startDate,
                endDate,
                page,
                pageSize);
        return Result.ok(pagedResult);
    }

    @ApiOperation(value = "查看用户详情", notes = "查看用户详情", httpMethod = "GET")
    @GetMapping("/detail")
    public Result userDetail(String userId) {
        return Result.ok(userService.queryUserById(userId));
    }

    @ApiOperation(value = "更改用户状态", notes = "更改用户状态", httpMethod = "POST")
    @PostMapping("/changeUserStatus")
    public Result changeUserStatus(String userId, Integer doStatus) {
        if (!UserStatus.isUserStatusValid(doStatus)) {
            return Result.r(Response.REQUEST_PARAM_ERROR);
        }
        userService.changeUserStatus(userId, doStatus);
        if (doStatus.equals(UserStatus.DISABLE.getVal())) {
            return Result.r(Response.DISABLE_SUCCESS);
        } else if (doStatus.equals(UserStatus.AVAILABLE.getVal())) {
            return Result.r(Response.AVAILABLE_SUCCESS);
        }
        return Result.r(Response.REQUEST_PARAM_ERROR);
    }

    @ApiOperation(value = "获得用户账户信息", notes = "获得用户账户信息", httpMethod = "GET")
    @GetMapping("/getAccountInfo")
    public Result getAccountInfo(String userId) {
        if (StringUtils.isBlank(userId)) {
            return Result.r(Response.UN_LOGIN);
        }
        // 根据用户id查询用户账户信息
        User user = getUser(userId);
        UserAccountInfoVO userAccountInfoVO = new UserAccountInfoVO();
        BeanUtils.copyProperties(user, userAccountInfoVO);
        return Result.ok(userAccountInfoVO);
    }

    @ApiOperation(value = "获得用户基本信息", notes = "获得用户基本信息", httpMethod = "GET")
    @GetMapping("/getUserBasicInfo")
    public Result getUserBasicInfo(@RequestParam String userId) {
        // 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return Result.r(Response.UN_LOGIN);
        }
        // 根据用户id查询用户基本信息
        User user = getUser(userId);
        UserBasicInfo ubi = new UserBasicInfo();
        BeanUtils.copyProperties(user, ubi);

        // 从redis中查询用户的粉丝数和关注数
        Integer myFollowCounts = getCountsFromRedis(RedisConst.REDIS_MY_FOLLOW_COUNTS + SysConst.SYMBOL_COLON + userId);
        Integer myFanCounts = getCountsFromRedis(RedisConst.REDIS_AUTHOR_FANS_COUNTS + SysConst.SYMBOL_COLON + userId);

        ubi.setMyFansCounts(myFanCounts);
        ubi.setMyFollowCounts(myFollowCounts);

        Integer available = user.getAvailable();
        if (UserStatus.AVAILABLE.getVal().equals(available)) {
            return Result.error(Response.DISABLE_USER.msg());
        }
        return Result.ok(ubi);
    }

    @ApiOperation(value = "更新用户信息", notes = "更新用户信息", httpMethod = "POST")
    @PostMapping("/updateUserInfo")
    public Result updateUserInfo(@RequestBody @Valid UserInfoBO userInfoBO) {
        // 执行更新操作
        userService.updateUserInfo(userInfoBO);
        return Result.r(Response.UPDATE_SUCCESS);
    }

    @ApiOperation(value = "根据用户ids查询用户信息", notes = "根据用户ids查询用户信息", httpMethod = "GET")
    @GetMapping("/queryByIds")
    public Result queryByIds(String userIds) {

        if (StringUtils.isBlank(userIds)) {
            return Result.r(Response.USER_NOT_EXIST_ERROR);
        }
        List<UserBasicInfo> userInfoList = new ArrayList<>();
        List<String> userIdList = JsonUtils.jsonToList(userIds, String.class);
        for (String id : userIdList) {
            User user = getUser(id);
            UserBasicInfo vo = new UserBasicInfo();
            BeanUtils.copyProperties(user, vo);
            userInfoList.add(vo);
        }
        return Result.ok(userInfoList);
    }

    @ApiOperation(value = "查询用户的登陆日志列表", notes = "查询用户的登陆日志列表", httpMethod = "GET")
    @GetMapping("/getLoginLogList")
    public Result getLoginLogList(@RequestParam String userId,
                                  @RequestParam Integer page,
                                  @RequestParam Integer pageSize) {

        checkPagingParams(page, pageSize);
        PagedResult res = loginLogService.getLoginLogList(userId, page, pageSize);
        return Result.ok(res);
    }

    private User getUser(String userId) {
        User user;
        // 缓存用户信息
        String jsonUser = redisCache.get(RedisConst.REDIS_USER_INFO + SysConst.SYMBOL_COLON + userId);
        if (StringUtils.isNotBlank(jsonUser)) {
            user = JsonUtils.jsonToPojo(jsonUser, User.class);
        } else {
            user = userService.queryUserById(userId);
            if (user != null) {
                // 判断user 不为空, 避免出现jsonUser = "null"的bug
                redisCache.set(RedisConst.REDIS_USER_INFO + SysConst.SYMBOL_COLON + userId, JsonUtils.objectToJson(user));
            } else {
                log.error("query user by id is null");
            }
        }
        return user;
    }

    @ApiOperation(value = "重置用户登录密码", notes = "重置用户登录密码", httpMethod = "POST")
    @PostMapping("/resetPassword/{id}")
    public Result resetPassword(@PathVariable("id") String userId) {
        userService.resetPasswords(userId);
        return Result.r(Response.RESET_PASSWORD_SUCCESS);

    }

    @ApiOperation(value = "修改用户登录密码", notes = "修改用户登录密码", httpMethod = "POST")
    @PostMapping("/alterPwd")
    public Result alterPwd(@RequestBody UpdatePwdBO updatePwdBO) {
        String tokenKey = RedisConst.REDIS_USER_TOKEN;
        String infoKey = RedisConst.REDIS_USER_INFO;
        User loginUser = getLoginUser(User.class, tokenKey, infoKey);
        String userId = loginUser.getId();
        if (StringUtils.isBlank(userId)) {
            return Result.r(Response.TICKET_INVALID);
        }
        User user = userService.queryUserById(userId);
        String password = user.getPassword();
        if (!Objects.equals(updatePwdBO.getOldPassword(), password)) {
            return Result.r(Response.PASSWORD_WRONG);
        }
        userService.alterPwd(userId, updatePwdBO.getNewPassword());
        return Result.r(Response.RESET_PASSWORD_SUCCESS);
    }

    @ApiOperation(value = "修改用户手机号码", notes = "修改用户手机号码", httpMethod = "POST")
    @PostMapping("/alterMobile")
    public Result alterMobile(@RequestBody UpdateMobileBO updateMobileBO) {
        String userId = updateMobileBO.getUserId();
        String newMobile = updateMobileBO.getNewMobile();
        if (StringUtils.isBlank(userId)) {
            return Result.r(Response.TICKET_INVALID);
        }
        if (StringUtils.isBlank(newMobile)) {
            return Result.r(Response.MOBILE_NULL);
        }
        User user = userService.queryUserById(userId);
        if (user == null) {
            return Result.r(Response.USER_NOT_EXIST_ERROR);
        }
        userService.alterMobile(user, newMobile);
        return Result.r(Response.ALTER_MOBILE_SUCCESS);
    }


    @ApiOperation(value = "获取全站用户数量", notes = "获取全站用户数量", httpMethod = "GET")
    @GetMapping("/getUserCounts")
    public Integer getUserCounts() {
        return userService.queryUserCounts();
    }
}
