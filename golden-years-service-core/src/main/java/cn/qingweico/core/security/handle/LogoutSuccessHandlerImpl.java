package cn.qingweico.core.security.handle;

import cn.qingweico.core.manager.AsyncManager;
import cn.qingweico.core.service.TokenService;
import cn.qingweico.entity.model.LoginUser;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.result.Result;
import cn.qingweico.util.ServletUtils;
import cn.qingweico.util.StringUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义退出处理类 返回成功
 *
 * @author zqw
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Resource
    private TokenService tokenService;

    /**
     * 退出处理
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
        }
        ServletUtils.renderString(response, JSON.toJSONString(Result.ok(Response.LOGOUT_SUCCESS)));
    }
}
