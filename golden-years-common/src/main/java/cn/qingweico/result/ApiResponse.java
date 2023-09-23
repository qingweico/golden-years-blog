package cn.qingweico.result;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * 返回数据结构entity
 *
 * @author zqw
 * @date 2023/9/23
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private @Builder.Default
    String code = "OK";
    private @Builder.Default
    String msg = "OK";
    private @Builder.Default
    Long now = Instant.now().getEpochSecond();
    private T data;
    private @JsonInclude(JsonInclude.Include.NON_NULL) List<String> errors;

    public ApiResponse(T data) {
        this.data = data;
    }
}
