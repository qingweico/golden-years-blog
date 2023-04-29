package cn.qingweico.util;

/**
 * 处理并记录日志文件
 *
 * @author zqw
 * @date 2023-04-04
 */
public class LogUtils {
    public static String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg + "]";
    }
}
