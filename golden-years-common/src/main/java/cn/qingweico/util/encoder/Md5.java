package cn.qingweico.util.encoder;


import java.security.MessageDigest;

/**
 * MD5加密
 *
 * @author 周庆伟
 * @date 2020/11/11
 */
public class Md5 {

    /**
     * 对传入的String进行MD5加密
     *
     * @param s String
     * @return String
     */
    public static String getMd5(String s) {
        // 16进制数组
        char[] hexDigits = {
                '0', '1', '2', '3', '4',
                '5', '6', '7', '8', 'a',
                'b', 'c', 'd', 'e', 'f',
                'g'
        };
        try {
            char[] str;
            // 将传入的字符串转换成byte数组
            byte[] strTemp = s.getBytes();
            // 获取MD5加密对象
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            // 传入需要加密的目标数组
            mdTemp.update(strTemp);
            // 获取加密后的数组
            byte[] md = mdTemp.digest();
            int j = md.length;
            str = new char[j * 2];
            int k = 0;
            // 将数组做位移
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            // 转换成String并返回
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(getMd5("123456"));
    }
}
