package cn.qingweico.core.security.handle;

import cn.qingweico.exception.TooManyAttemptsException;
import cn.qingweico.result.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Collections;

/**
 * 登陆失败处理类
 *
 * @author zqw
 * @date 2023/9/23
 */
@Slf4j
@Component
public class ApiAuthenticationFailHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter printWriter = response.getWriter();
        ApiResponse<?> apiResponse = ApiResponse
                .builder()
                .code(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .msg(exception.getMessage())
                .data(null)
                .errors(Collections.emptyList())
                .build();
        log.error("onAuthenticationFailure:", exception);
        printWriter.append(objectMapper.writeValueAsString(apiResponse));
        if (exception instanceof TooManyAttemptsException) {
            log.error("TooManyAttemptsException: {}", exception.getMessage());
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}
