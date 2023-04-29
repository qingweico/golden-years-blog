package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.RoleMapper;
import cn.qingweico.admin.mapper.UserRoleRelMapper;
import cn.qingweico.admin.service.RoleService;
import cn.qingweico.entity.SysUserRoleRel;
import cn.qingweico.pojo.Role;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zqw
 * @date 2022/3/23
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleRelMapper userRoleRelMapper;

    @Override
    public List<Role> queryRoleBySysUserId(String sysUserId) {
        SysUserRoleRel sysUserRoleRel = new SysUserRoleRel();
        sysUserRoleRel.setSysUserId(sysUserId);
        List<SysUserRoleRel> userRoles = userRoleRelMapper.select(sysUserRoleRel);
        List<Role> roles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userRoles)) {
            for (SysUserRoleRel userRole : userRoles) {
                Role role = roleMapper.selectByPrimaryKey(userRole.getRoleId());
                roles.add(role);
            }
            return roles;
        }
        return new ArrayList<>(0);
    }
}
