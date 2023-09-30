package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.PermissionMapper;
import cn.qingweico.admin.service.SysPermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author zqw
 * @date 2023/9/29
 */
@Service
public class SysPermissionServiceImpl implements SysPermissionService {


    @Resource
    private PermissionMapper permissionMapper;


    /**
     * 获取用户的权限
     * @param userId 用户id
     * @return Set<String> 权限集合
     */
    @Override
    public Set<String> selectPermsByUserId(String userId) {
        return permissionMapper.selectPermsByUserId(userId);
    }
}
