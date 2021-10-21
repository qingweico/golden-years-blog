package cn.qingweico.user.service;

import cn.qingweico.pojo.AppUser;
import cn.qingweico.pojo.bo.UpdateUserInfoBO;

/**
 * @author:qiming
 * @date: 2021/9/6
 */
public interface UserService {

    /**
     * 判断用户是否存在 存在则返回user信息
     * @param mobile mobile
     * @return user
     */
    AppUser queryMobileIsExist(String mobile);

    /**
     * 创建用户 新增一条用户记录到数据库
     * @return user
     */
    AppUser createUser(String mobile);

    /**
     * 根据用户id查询用户信息
     * @param userId String -> userId
     * @return AppUser
     */
    AppUser queryUserById(String userId);

    /**
     * 用户修改信息, 完善资料, 并且激活账户
     * @param updateUserInfoBO UpdateUserInfoBO
     */
    void updateUserInfo(UpdateUserInfoBO updateUserInfoBO);

    /**
     * 通过用户名或者手机号或者用户邮箱查询用户信息
     * @return AppUser
     */
    AppUser queryUserByAuth(String auth);
}
