package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.RoleMapper;
import cn.qingweico.admin.service.RoleService;
import cn.qingweico.pojo.Role;
import org.springframework.stereotype.Service;

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

    @Override
    public List<Role> listByIds(List<String> ids) {
        List<Role> roleList = new ArrayList<>();
        for (String id : ids) {
            Role role = roleMapper.selectByPrimaryKey(id);
            roleList.add(role);
        }
        return roleList;
    }
}
