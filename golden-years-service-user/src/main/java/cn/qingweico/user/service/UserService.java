package cn.qingweico.user.service;

import cn.qingweico.entity.User;
import cn.qingweico.entity.model.UserInfoBO;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.util.PagedResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/6
 */
public interface UserService extends IService<User> {
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
    PagedResult queryUserList(String nickname,
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
     * 判断手机是否存在
     *
     * @param mobile 用户手机号
     * @return 用户信息
     */
    User queryUserByMobile(String mobile);

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
     * @param userId 用户ID
     * @param newPassword 新的密码
     */
    void alterPwd(String userId, String newPassword);

    /**
     * 更新用户手机号
     * @param user 用户信息
     * @param newMobile 用户新的手机号码
     */
    void alterMobile(User user, String newMobile);


    /**
     * 获取全站用户的数量
     * {@link UserStatus}
     *
     * @return {@code Integer} 全站用户的数量
     */
    Integer queryUserCounts();
}
