package cn.qingweico.admin.service;

import cn.qingweico.pojo.Role;

import java.util.List;

/**
 * @author zqw
 * @date 2022/3/23
 */
public interface RoleService {
   /**
    * 根据角色ids获取角色信息
    *
    * @param ids ids
    * @return List<Role>
    */
   List<Role> listByIds(List<String> ids);
}
