package cn.qingweico.admin.mapper;

import cn.qingweico.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author zqw
 * @date 2023/9/29
 */
@Repository
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectPermsByUserId(String userId);
}
