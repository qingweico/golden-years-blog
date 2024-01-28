package cn.qingweico.admin.service.impl;

import  cn.qingweico.admin.mapper.RoleMapper;
import cn.qingweico.admin.mapper.UserRoleRelMapper;
import cn.qingweico.admin.service.RoleService;
import cn.qingweico.entity.SysRole;
import cn.qingweico.entity.SysUserRoleRel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zqw
 * @date 2022/3/23
 */
@Service
public class SysRoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleRelMapper userRoleRelMapper;

    @Override
    public List<SysRole> queryRoleBySysUserId(String sysUserId) {
        LambdaQueryWrapper<SysUserRoleRel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUserRoleRel::getSysUserId, sysUserId);

        List<SysUserRoleRel> userRoles = userRoleRelMapper.selectList(lambdaQueryWrapper);
        List<SysRole> roles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userRoles)) {
            for (SysUserRoleRel userRole : userRoles) {
                SysRole role = roleMapper.selectById(userRole.getRoleId());
                roles.add(role);
            }
            return roles;
        }
        return new ArrayList<>(0);
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(String userId) {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (ObjectUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getCode().trim().split(",")));
            }
        }
        return permsSet;
    }


}
