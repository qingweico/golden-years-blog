package cn.qingweico.user.controller;

import cn.qingweico.api.controller.BaseController;
import cn.qingweico.api.controller.user.UserControllerApi;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.AppUser;
import cn.qingweico.pojo.bo.UpdateUserInfoBO;
import cn.qingweico.pojo.vo.UserAccountInfoVO;
import cn.qingweico.pojo.vo.UserBasicInfoVO;
import cn.qingweico.user.service.LoginLogService;
import cn.qingweico.user.service.UserService;
import cn.qingweico.user.service.impl.UserServiceImpl;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/5
 */
@RestController
public class UserController extends BaseController implements UserControllerApi {

    @Resource
    private UserService userService;

    @Resource
    private LoginLogService loginLogService;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public GraceJsonResult getAccountInfo(String userId) {
        // 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        // 根据用户id查询用户账户信息
        AppUser user = getUser(userId);
        UserAccountInfoVO userAccountInfoVO = new UserAccountInfoVO();
        BeanUtils.copyProperties(user, userAccountInfoVO);
        return GraceJsonResult.ok(userAccountInfoVO);
    }

    @Override
    public GraceJsonResult getUserBasicInfo(String userId) {
        // 判断参数不能为空
        if (StringUtils.isBlank(userId)) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        // 根据用户id查询用户基本信息
        AppUser user = getUser(userId);
        UserBasicInfoVO userVO = new UserBasicInfoVO();
        BeanUtils.copyProperties(user, userVO);


        // 从redis中查询用户的粉丝数和关注数
        Integer myFollowCounts = getCountsFromRedis(REDIS_MY_FOLLOW_COUNTS + ":" + userId);
        Integer myFanCounts = getCountsFromRedis(REDIS_WRITER_FANS_COUNTS + ":" + userId);

        userVO.setMyFansCounts(myFanCounts);
        userVO.setMyFollowCounts(myFollowCounts);

        Integer userStatus = userVO.getActiveStatus();
        if (userStatus.equals(UserStatus.INACTIVE.type)) {
            return new GraceJsonResult(ResponseStatusEnum.WELCOME, userVO);
        } else if (userStatus.equals(UserStatus.FROZEN.type)) {
            return new GraceJsonResult(ResponseStatusEnum.ACCOUNT_ILLEGAL, userVO);
        }
        return GraceJsonResult.ok(userVO);
    }

    @Override
    public GraceJsonResult updateUserInfo(UpdateUserInfoBO updateUserInfoBO) {
        // 执行更新操作
        userService.updateUserInfo(updateUserInfoBO);
        return new GraceJsonResult(ResponseStatusEnum.UPDATE_SUCCESS);
    }

    @Override
    public GraceJsonResult queryByIds(String userIds) {

        if (StringUtils.isBlank(userIds)) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }
        List<UserBasicInfoVO> publishList = new ArrayList<>();
        List<String> userIdList = JsonUtils.jsonToList(userIds, String.class);
        for (String id : userIdList) {
            AppUser user = getUser(id);
            UserBasicInfoVO vo = new UserBasicInfoVO();
            BeanUtils.copyProperties(user, vo);
            publishList.add(vo);
        }
        return GraceJsonResult.ok(publishList);
    }

    @Override
    public GraceJsonResult getLoginLogList(String userId,
                                           Integer page,
                                           Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PagedGridResult res = loginLogService.getLoginLogList(userId, page, pageSize);

        return GraceJsonResult.ok(res);
    }

    private AppUser getUser(String userId) {
        AppUser user;
        // 缓存用户信息
        String jsonUser = redisOperator.get(REDIS_USER_INFO + ":" + userId);
        if (StringUtils.isNotBlank(jsonUser)) {
            user = JsonUtils.jsonToPojo(jsonUser, AppUser.class);
        } else {
            user = userService.queryUserById(userId);
            log.info("查询数据库啦!");
            redisOperator.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));
        }
        return user;
    }
}
