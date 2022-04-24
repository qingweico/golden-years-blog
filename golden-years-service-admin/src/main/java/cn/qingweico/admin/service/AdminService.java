package cn.qingweico.admin.service;

import cn.qingweico.pojo.Admin;
import cn.qingweico.pojo.bo.OperatorAdminBO;
import cn.qingweico.util.PagedGridResult;

/**
 * @author zqw
 * @date 2021/9/9
 */
public interface AdminService {

    /**
     * 根据用户名查询admin信息
     *
     * @param username admin的用户名
     * @return AdminUser
     */
    Admin queryAdminByUsername(String username);

    /**
     * 根据用户id查询admin信息
     *
     * @param id admin的id
     * @return AdminUser
     */
    Admin queryAdminById(String id);


    /**
     * 新增admin用户信息
     *
     * @param operatorAdminBO NewAdminBO
     */
    void createAdminUser(OperatorAdminBO operatorAdminBO);

    /**
     * 分页查询admin列表
     *
     * @param page     page
     * @param pageSize 每页显示的数目
     * @return PagedGridResult
     */
    PagedGridResult queryAdminList(Integer page, Integer pageSize);

    /**
     * 更改管理员账户密码
     *
     * @param id          管理员id
     * @param newPassword 新密码
     */
    void alterPwd(String id, String newPassword);


    /**
     * 更新管理员个人信息
     *
     * @param user {@link OperatorAdminBO}
     */
    void updateUserProfile(OperatorAdminBO user);

    /**
     * 检查管理员用户名的唯一性
     *
     * @param username 管理员用户名
     */
    void checkUsernameIsPresent(String username);

    /**
     * 保存登录token
     *
     * @param admin Admin
     * @param token token
     */
    void doSaveToken(Admin admin, String token);

    /**
     * 保存登录信息
     * @param id id
     */
    void doSaveLoginLog(String id);
}
