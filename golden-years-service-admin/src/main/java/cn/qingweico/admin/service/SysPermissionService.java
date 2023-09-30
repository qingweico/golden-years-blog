package cn.qingweico.admin.service;

import cn.qingweico.entity.SysRole;
import cn.qingweico.entity.SysUser;

import java.util.List;
import java.util.Set;

/**
 * @author zqw
 * @date 2023/9/29
 */
public interface SysPermissionService {
    /**
     * 获取用户的权限
     * @param userId 用户id
     * @return Set<String> 权限集合
     */
    Set<String> selectPermsByUserId(String userId);


}
