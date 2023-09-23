package cn.qingweico.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author zqw
 * @date 2023/9/23
 */
public class TooManyAttemptsException extends AuthenticationException {

    public TooManyAttemptsException(String msg, Throwable t) {
        super(msg, t);
    }

    public TooManyAttemptsException(String msg) {
        super(msg);
    }
}
