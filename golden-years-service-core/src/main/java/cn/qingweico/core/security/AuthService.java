package cn.qingweico.core.security;

import cn.qingweico.entity.SysUser;
import cn.qingweico.entity.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zqw
 * @date 2023/9/29
 */
@Service
@Slf4j
public class AuthService {


    public LoginUser getLoginUser(SysUser user) {
        return new LoginUser(user, getUserPermission(user.getId()));
    }


    public Set<String> getUserPermission(String userId) {
        return new HashSet<>();
    }

}
