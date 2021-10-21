package cn.qingweico.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:qiming
 * @date: 2021/9/10
 */
public class UrlUtil {

    /**
     * 验证是否是URL
     * @param url 目标url
     * @return 是否为url
     */
    public static boolean verifyUrl(String url){

        // URL验证规则
//        String regEx ="[A-Za-z]+://[A-Za-z0-9-_]+\\\\.[A-Za-z0-9-_%&\\?\\/.=]+";
        String regEx = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])" +
                "+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        // 字符串是否与正则表达式相匹配
        return matcher.matches();

    }

    public static void main(String[] args) {
        boolean res =
                verifyUrl("http://admin.imoocnews.com:9090/imooc-news/admin/friendLinks.html");
        System.out.println(res);
    }

}
