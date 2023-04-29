package cn.qingweico.exception.user;

/**
 * 用户信息异常类
 *
 * @author zqw
 */
public class UserException extends RuntimeException  {
    private static final long serialVersionUID = 1L;

    public UserException(String message) {
        super(message);
    }
}
