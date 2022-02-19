package cn.qingweico.user.service;

import cn.qingweico.util.PagedGridResult;

import java.util.Date;

/**
 * @author:qiming
 * @date: 2021/9/11
 */
public interface AppUserService {

    /**
     * 查询所有用户
     * 根据条件进行筛选
     * @param nickname     用户昵称
     * @param status       用户状态
     * @param startDate    开始时间
     * @param endDate      截至时间
     * @param page         起始分页
     * @param pageSize     每页数目
     * @return             用户列表
     */
    PagedGridResult queryAllUserList(String nickname,
                                     Integer status,
                                     Date startDate,
                                     Date endDate,
                                     Integer page,
                                     Integer pageSize);


    /**
     * 冻结或者解冻用户
     * @param userId 用户id
     * @param doStatus doStatus
     */
    void freezeUserOrNot(String userId, Integer doStatus);
}
