package cn.qingweico.user.service;

import cn.qingweico.util.PagedGridResult;

/**
 * 用户登陆日志
 *
 * @author:qiming
 * @date: 2021/9/22
 */
public interface LoginLogService {

    /**
     * 用户登陆 保存用户的登陆日志
     *
     * @param userId 用户id
     */
    void saveUserLoginLog(String userId);

    /**
     * 查询用户的登陆日志列表
     *
     * @param userId 用户id
     * @return PagedGridResult
     */
    PagedGridResult getLoginLogList(String userId, Integer page, Integer pageSize);

}
