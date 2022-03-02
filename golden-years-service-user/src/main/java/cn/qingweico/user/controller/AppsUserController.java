package cn.qingweico.user.controller;

import cn.qingweico.enums.UserStatus;
import cn.qingweico.global.Constants;
import cn.qingweico.global.RedisConf;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.user.service.AppUserService;
import cn.qingweico.api.controller.user.AppsUserControllerApi;
import cn.qingweico.api.controller.BaseController;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.PagedGridResult;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zqw
 * @date 2021/9/11
 */
@RestController
public class AppsUserController extends BaseController implements AppsUserControllerApi {

    @Resource
    private AppUserService appUserService;

    @Resource
    private UserService userService;

    @Override
    public GraceJsonResult query(String nickname,
                                 Integer status,
                                 Date startDate,
                                 Date endDate,
                                 Integer page,
                                 Integer pageSize) {

        if (page == null) {
            page = Constants.COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = Constants.COMMON_PAGE_SIZE;
        }
        PagedGridResult pagedGridResult = appUserService.queryAllUserList(nickname,
                status,
                startDate,
                endDate,
                page,
                pageSize);
        return GraceJsonResult.ok(pagedGridResult);
    }

    @Override
    public GraceJsonResult userDetail(String userId) {
        return GraceJsonResult.ok(userService.queryUserById(userId));
    }

    @Override
    public GraceJsonResult freezeUserOrNot(String userId, Integer doStatus) {

        if (!UserStatus.isUserStatusValid(doStatus)) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.USER_STATUS_ERROR);
        }
        appUserService.freezeUserOrNot(userId, doStatus);

        // 刷新用户状态
        redisOperator.del(RedisConf.REDIS_USER_INFO + Constants.SYMBOL_COLON + userId);

        if (doStatus.equals(UserStatus.FROZEN.type)) {
            return new GraceJsonResult(ResponseStatusEnum.FREEZE_SUCCESS);
        } else if (doStatus.equals(UserStatus.ACTIVE.type)) {
            return new GraceJsonResult(ResponseStatusEnum.ACTIVATE_SUCCESS);
        }
        return GraceJsonResult.errorCustom(ResponseStatusEnum.REQUEST_PARAM_ERROR);

    }
}