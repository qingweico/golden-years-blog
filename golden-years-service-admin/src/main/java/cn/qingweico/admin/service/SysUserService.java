package cn.qingweico.admin.service;

import cn.qingweico.admin.model.bo.OperatorSysUserBO;
import cn.qingweico.entity.SysUser;
import cn.qingweico.util.PagedResult;

/**
 * @author zqw
 * @date 2021/9/9
 */
public interface SysUserService {

    /**
     * 根据用户名[系统用户名唯一]查询SysUser信息
     *
     * @param username admin的用户名
     * @return SysUser
     */
    SysUser querySysUserByUsername(String username);

    /**
     * 根据用户id查询SysUser信息
     *
     * @param id SysUser的id
     * @return SysUser
     */
    SysUser querySysUserById(String id);


    /**
     * 新增SysUser用户信息
     *
     * @param operatorSysUserBO operatorSysUserBO
     */
    void createSysUser(OperatorSysUserBO operatorSysUserBO);

    /**
     * 分页查询SysUser列表
     *
     * @param page     page
     * @param pageSize 每页显示的数目
     * @return PagedGridResult
     */
    PagedResult querySysUserList(Integer page, Integer pageSize);

    /**
     * 更改SysUser账户密码
     *
     * @param id          SysUserId
     * @param newPassword 新密码
     */
    void alterPwd(String id, String newPassword);


    /**
     * 更新SysUser信息
     *
     * @param sysUser {@link OperatorSysUserBO}
     */
    void updateSysUserProfile(OperatorSysUserBO sysUser);

    /**
     * 检查SysUser用户名的唯一性
     *
     * @param username SysUser username
     */
    void checkUsernamePresent(String username);

    /**
     * 保存登录token
     *
     * @param sysUser sysUser
     * @param token token
     */
    void doSaveToken(SysUser sysUser, String token);

    /**
     * 保存登录信息
     * @param id id
     */
    void doSaveLoginLog(String id);
}
