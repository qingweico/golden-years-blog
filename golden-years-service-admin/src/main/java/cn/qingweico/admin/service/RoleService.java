package cn.qingweico.admin.service;

import cn.qingweico.pojo.Role;

import java.util.List;

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

   List<Role> queryRoleBySysUserId(String sysUserId);
}
