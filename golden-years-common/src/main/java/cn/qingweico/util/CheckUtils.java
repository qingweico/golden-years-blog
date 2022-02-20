package cn.qingweico.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/
 * /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/
 * @author zqw
 * @date 2021/11/6
 */
public class CheckUtils {
    static String CHECK_EMAIL_REGEX = "^([a-z0-9A-Z]+[-|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    static String CHECK_MOBILE_NUMBER_REGEX
            = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
    static String CHECK_URL_REGEX = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~/])" +
            "+(\\??(([A-Za-z0-9-~]+=?)([A-Za-z0-9-~]*)&?)*)$";

    /**
     * 校验邮箱
     *
     * @param email email
     * @return 邮箱是否合法
     */
    public static boolean checkEmail(String email) {
        boolean flag;
        Pattern regex = Pattern.compile(CHECK_EMAIL_REGEX);
        Matcher matcher = regex.matcher(email);
        flag = matcher.matches();
        return flag;
    }

    /**
     * 校验手机号
     *
     * @param mobileNumber 手机号
     * @return 手机号码是否合法
     */
    public static boolean checkMobileNumber(String mobileNumber) {
        boolean flag;
        Pattern regex = Pattern.compile(CHECK_MOBILE_NUMBER_REGEX);
        Matcher matcher = regex.matcher(mobileNumber);
        flag = matcher.matches();
        return flag;
    }

    /**
     * 验证是否是URL
     *
     * @param url 目标url
     * @return 是否为url
     */
    public static boolean checkUrl(String url) {
        boolean flag;
        Pattern pattern = Pattern.compile(CHECK_URL_REGEX);
        Matcher matcher = pattern.matcher(url);
        flag = matcher.matches();
        return flag;
    }
}
