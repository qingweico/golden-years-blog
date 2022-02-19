package cn.qingweico.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zqw
 * @date 2021/9/25
 */
public class HttpServletRequestUtil {
    public static int getInteger(HttpServletRequest request, String key) {
        try {
            return Integer.decode(request.getParameter(key));
        } catch (Exception e) {
            return -1;
        }
    }

    public static Long getLong(HttpServletRequest request, String key) {
        try {
            return Long.valueOf(request.getParameter(key));
        } catch (Exception e) {
            return -1L;
        }
    }

    public static Boolean getBoolean(HttpServletRequest request, String key) {
        try {
            return Boolean.valueOf(request.getParameter(key));
        } catch (Exception e) {
            return false;
        }
    }

    public static String getString(HttpServletRequest request, String key) {
        try {
            String result = request.getParameter(key);
            if (result != null) {
                result = result.trim();
            }
            if ("".equals(result)) {
                return null;
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
