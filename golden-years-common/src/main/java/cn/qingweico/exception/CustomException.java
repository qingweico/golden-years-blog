package cn.qingweico.exception;

import cn.qingweico.result.ResponseStatusEnum;

/**
 * 自定义异常
 *
 * @author zqw
 * @date 2021/9/6
 */
public class CustomException extends RuntimeException {
    private final ResponseStatusEnum responseStatus;

    public CustomException(ResponseStatusEnum responseStatus) {
        super("异常状态码为: " + responseStatus.status() + ";" +
                "具体异常信息为: " + responseStatus.msg());
        this.responseStatus = responseStatus;
    }

    public ResponseStatusEnum getResponseStatus() {
        return responseStatus;
    }
}
