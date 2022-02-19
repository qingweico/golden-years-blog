package cn.qingweico.admin.service;

import cn.qingweico.pojo.AdminUser;
import cn.qingweico.pojo.bo.NewAdminBO;
import cn.qingweico.util.PagedGridResult;

/**
 * @author zqw
 * @date 2021/9/9
 */
public interface AdminUserService {


    /**
     * 根据用户名查询admin信息
     *
     * @param username admin的用户名
     * @return AdminUser
     */
    AdminUser queryAdminByUsername(String username);

    /**
     * 新增admin用户信息
     *
     * @param newAdminBO NewAdminBO
     */
    void createAdminUser(NewAdminBO newAdminBO);

    /**
     * 分页查询admin列表
     *
     * @param page     page
     * @param pageSize 每页显示的数目
     * @return PagedGridResult
     */
    PagedGridResult queryAdminList(Integer page, Integer pageSize);
}
