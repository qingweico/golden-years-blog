package cn.qingweico.exception;

import cn.qingweico.result.Response;

/**
 * 自定义异常
 *
 * @author zqw
 * @date 2021/9/6
 */
public class CustomException extends RuntimeException {
    private final Response responseStatus;

    public CustomException(Response responseStatus) {
        super("异常状态码为: " + responseStatus.code() + ";" +
                "具体异常信息为: " + responseStatus.msg());
        this.responseStatus = responseStatus;
    }

    public Response getResponseStatus() {
        return responseStatus;
    }
}
