package cn.qingweico.core.security.handle;

import cn.qingweico.result.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * @author zqw
 * @date 2023/9/23
 */
@Component
@Slf4j
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
            throws IOException {
        ApiResponse<?> apiResponse = ApiResponse
                .builder()
                .code(HttpStatus.FORBIDDEN.getReasonPhrase())
                .msg(HttpStatus.FORBIDDEN.getReasonPhrase())
                .data(exception.getMessage())
                .errors(Collections.emptyList())
                .build();
        log.error("ApiAccessDeniedHandler: {}", exception.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(response.getWriter(), apiResponse);
    }
}
