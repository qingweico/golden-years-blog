package cn.qingweico.admin.service;

import cn.qingweico.entity.SysRole;

import java.util.List;
import java.util.Set;

/**
 * @author zqw
 * @date 2022/3/23
 */
public interface RoleService {
   /**
    * 根据sysUserId查询用户对应的角色(集合)
    *
    * @param sysUserId sysUserId
    * @return List<Role>
    */

   List<SysRole> queryRoleBySysUserId(String sysUserId);

   /**
    * 根据用户ID查询角色权限
    *
    * @param userId 用户ID
    * @return 权限列表
    */
   Set<String> selectRolePermissionByUserId(String userId);
}
