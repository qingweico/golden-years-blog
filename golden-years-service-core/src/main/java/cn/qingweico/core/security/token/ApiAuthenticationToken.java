package cn.qingweico.core.security.token;

import cn.qingweico.entity.SysUser;
import cn.qingweico.entity.model.LoginUser;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author zqw
 * @date 2023/9/23
 */
@Builder
public class ApiAuthenticationToken implements Authentication {


    private static final long serialVersionUID = 6712621209562892992L;

    private @Getter
    LoginUser loginUser;

    private @Getter String token;

    @Getter
    private Serializable data;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return loginUser.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return user;
    }

    @Override
    public Object getPrincipal() {
        return getCredentials();
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (!isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token unauthenticated");
        }
    }

    @Override
    public String getName() {
        return user.getUsername();
    }
}
