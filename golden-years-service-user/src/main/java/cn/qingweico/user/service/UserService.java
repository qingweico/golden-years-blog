package cn.qingweico.user.service;

import cn.qingweico.enums.UserStatus;
import cn.qingweico.pojo.User;
import cn.qingweico.pojo.bo.UpdatePwdBO;
import cn.qingweico.pojo.bo.UserInfoBO;
import cn.qingweico.util.PagedGridResult;

import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/6
 */
public interface UserService {
    /**
     * 查询所有用户
     * 根据条件进行筛选
     *
     * @param nickname  用户昵称
     * @param status    用户状态
     * @param mobile    用户手机号
     * @param startDate 开始时间
     * @param endDate   截至时间
     * @param page      起始分页
     * @param pageSize  每页数目
     * @return 用户列表
     */
    PagedGridResult queryUserList(String nickname,
                                  Integer status,
                                  String mobile,
                                  Date startDate,
                                  Date endDate,
                                  Integer page,
                                  Integer pageSize);


    /**
     * 冻结或者解冻用户
     *
     * @param userId   用户id
     * @param doStatus doStatus
     */
    void freezeUserOrNot(String userId, Integer doStatus);

    /**
     * 判断用户是否存在, 存在则返回user信息
     *
     * @param mobile 用户手机号
     * @return user
     */
    User queryMobileIsPresent(String mobile);

    /**
     * 创建用户 新增一条用户记录到数据库
     *
     * @param mobile 用户手机号
     * @return user
     */
    User createUser(String mobile);

    /**
     * 根据用户id查询用户信息
     *
     * @param userId String -> userId
     * @return AppUser
     */
    User queryUserById(String userId);

    /**
     * 用户修改信息, 完善资料, 并且激活账户
     *
     * @param userInfoBO UpdateUserInfoBO
     */
    void updateUserInfo(UserInfoBO userInfoBO);

    /**
     * 通过用户名或者手机号或者用户邮箱查询用户信息
     *
     * @param auth 用户名 | 手机号 | 用户邮箱
     * @return AppUser
     */
    User queryUserByAuth(String auth);


    /**
     * 保存用户token
     *
     * @param user  user
     * @param token token
     */
    void doSaveUserAuthToken(User user, String token);

    /**
     * 保存用户登录日志
     *
     * @param userId 用户id
     */
    void doSaveLoginLog(String userId);

    /**
     * 重置用户密码
     *
     * @param userId 用户id
     */
    void resetPasswords(String userId);

    /**
     * 更新用户密码
     *
     * @param updatePwdBO {@link UpdatePwdBO}
     */
    void alterPwd(UpdatePwdBO updatePwdBO);


    /**
     * 获取全站用户的数量
     * {@link UserStatus} 只获取已激活和冻结的用户
     *
     * @return {@code Integer} 全站用户的数量
     */
    Integer queryUserCounts();
}
