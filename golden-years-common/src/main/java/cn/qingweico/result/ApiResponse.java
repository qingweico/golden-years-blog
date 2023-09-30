package cn.qingweico.result;


import lombok.*;

import java.time.Instant;

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

    public ApiResponse(T data) {
        this.data = data;
    }
}
