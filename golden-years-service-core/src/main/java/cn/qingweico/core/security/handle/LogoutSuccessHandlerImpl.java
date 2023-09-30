package cn.qingweico.core.security.handle;

import cn.qingweico.core.security.TokenService;
import cn.qingweico.entity.model.LoginUser;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.ApiResponse;
import cn.qingweico.result.Response;
import cn.qingweico.result.Result;
import cn.qingweico.util.ServletUtils;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自定义退出处理类 返回成功
 *
 * @author zqw
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutHandler, LogoutSuccessHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    private TokenService tokenService;

    /**
     * 退出处理
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        ApiResponse<?> apiResponse = ApiResponse
                .builder()
                .code(HttpStatus.OK.getReasonPhrase())
                .msg("logout success")
                .build();

        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(apiResponse));
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorization)) {
            return;
        }
        if (!authorization.startsWith(SysConst.TOKEN_PREFIX)) {
            return;
        }
        String jwtAuthenticationToken = authorization.substring(7);
        LoginUser loginUser = tokenService.getLoginUser(request);
        if(!ObjectUtils.isEmpty(loginUser) && StringUtils.equals(jwtAuthenticationToken,loginUser.getToken())) {
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // TODO 记录日志
        }
        ServletUtils.renderString(response, JSON.toJSONString(Result.ok(Response.LOGOUT_SUCCESS)));
    }
}
