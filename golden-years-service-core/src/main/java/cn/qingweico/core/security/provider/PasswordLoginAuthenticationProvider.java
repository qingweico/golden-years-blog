package cn.qingweico.core.security.provider;

import cn.qingweico.core.config.limit.RateLimiterHandler;
import cn.qingweico.core.security.token.ApiAuthenticationToken;
import cn.qingweico.core.security.token.LoginAuthenticationToken;
import cn.qingweico.entity.model.LoginLimit;
import cn.qingweico.entity.model.LoginUser;
import cn.qingweico.exception.TooManyAttemptsException;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;

/**
 * @author zqw
 * @date 2023/9/30
 */
public class PasswordLoginAuthenticationProvider implements AuthenticationProvider {

    @Setter
    private PasswordEncoder passwordEncoder;


    @Setter
    private RateLimiterHandler rateLimiterHandler;


    private static final String KEY = "PasswordLogin";

    @Setter
    private LoginLimit loginLimit = new LoginLimit(60, 20);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!rateLimiterHandler.limit(this.getClass().getSimpleName(), KEY, loginLimit.getLimitPeriod(), loginLimit.getLimitCount())) {
            throw new TooManyAttemptsException("Too many attempts");
        }
        LoginAuthenticationToken token = (LoginAuthenticationToken) authentication;
        LoginUser loginUser = authService.getLoginUser(token);
        if (authService.tooManyAttempts(loginUser.getId())) {
            throw new TooManyAttemptsException("Too many attempts");
        }
        String presentedPassword = authentication.getCredentials().toString();
        String decodePassword = presentedPassword;
        if (!ObjectUtils.isEmpty(passwordDecoder)) {
            decodePassword = passwordDecoder.decode(presentedPassword);
        }
        if (!this.passwordEncoder.matches(decodePassword,
                loginUser.getPassword())) {
            authService.logUserAction(loginUser, UserLogCommand.LOGIN_FAIL, UserLogCredentialsType.PASSWORD, token.getIp());
            authService.addAttempts(loginUser.getId());
            throw new BadCredentialsException("invalid identifier or certificate");
        }
        if (!loginUser.isEnabled()) {
            authService.logUserAction(loginUser, UserLogCommand.LOGIN_FAIL, UserLogCredentialsType.PASSWORD, token.getIp());
            authService.addAttempts(loginUser.getId());
            throw new DisabledException("The user has been disabled");
        }
        TzUser user = authService.getTzUser(loginUser);
        String jwttoken = authService.generateToken(user);
        //缓存用户
        authService.storeUser(user, jwttoken);
        //记录登录动作
        authService.logUserAction(user, UserLogCommand.LOGIN, UserLogCredentialsType.PASSWORD, token.getIp());
        //清空尝试次数
        authService.deleteAttempts(user.getAccountId());
        return ApiAuthenticationToken.builder()
                .user(user).token(jwttoken).build();
    }

}
