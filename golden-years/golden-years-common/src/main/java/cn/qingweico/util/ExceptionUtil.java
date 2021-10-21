package cn.qingweico.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 错误信息工具类
 *
 * @author:qiming
 * @date: 2021/9/22
 */
public class ExceptionUtil {

    /**
     * 获取异常信息 ---> System.out
     *
     * @param t Throwable
     */
    public static String getExceptionMessage(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }

    public static String getRootErrorMessage(Exception e) {
        Throwable root = ExceptionUtils.getRootCause(e);
        root = (root == null ? e : root);
        if (root == null) {
            return "";
        }
        String message = root.getMessage();
        if (message == null) {
            return "null";
        }
        return StringUtils.defaultString(message);
    }
}
