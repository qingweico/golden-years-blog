package cn.qingweico.user.service;

import cn.qingweico.util.PagedGridResult;

/**
 * 用户登陆日志
 *
 * @author zqw
 * @date 2021/9/22
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
     * @param page 起始分页数(从1开始)
     * @param pageSize 每页的数量
     * @return PagedGridResult
     */
    PagedGridResult getLoginLogList(String userId, Integer page, Integer pageSize);

    /**
     * 定时(一个月内)清理用户的登陆日志信息
     */
    void cleanLoginLog();

}
