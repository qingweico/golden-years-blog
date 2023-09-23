package cn.qingweico.core.security.handle;

import cn.qingweico.core.security.token.ApiAuthenticationToken;
import cn.qingweico.result.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Collections;

/**
 * @author zqw
 * @date 2023/9/23
 */
@Slf4j
@Component
public class ApiAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        ApiAuthenticationToken apiAuthenticationToken=(ApiAuthenticationToken) authentication;
        ApiResponse<?> apiResponse = ApiResponse
                .builder()
                .code(HttpStatus.OK.getReasonPhrase())
                .msg("login success")
                .data(apiAuthenticationToken.getToken())
                .errors(Collections.emptyList())
                .build();

        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter printWriter = response.getWriter();
        printWriter.append(objectMapper.writeValueAsString(apiResponse));
    }
}
