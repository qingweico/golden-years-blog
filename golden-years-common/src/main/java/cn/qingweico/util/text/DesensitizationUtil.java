package cn.qingweico.util.text;

/**
 * 文本脱敏工具类
 *
 * @author zqw
 * @date 2021/9/6
 */
public class DesensitizationUtil {
    private static final int SIZE = 6;
    private static final int MIN = 2;
    private static final String SYMBOL = "*";


    /**
     * 通用脱敏方法
     *
     * @param value String
     * @return String
     */
    public static String commonDisplay(String value) {
        if (null == value || "".equals(value)) {
            return value;
        }
        int len = value.length();
        int pamaone = len / 2;
        int pamatwo = pamaone - 1;
        int pamathree = len % 2;
        StringBuilder stringBuilder = new StringBuilder();
        if (len <= MIN) {
            if (pamathree == 1) {
                return SYMBOL;
            }
            stringBuilder.append(SYMBOL);
            stringBuilder.append(value.charAt(len - 1));
        } else {
            if (pamatwo <= 0) {
                stringBuilder.append(value.charAt(0));
                stringBuilder.append(SYMBOL);
                stringBuilder.append(value.charAt(len - 1));

            } else if (pamatwo >= SIZE / MIN && SIZE + 1 != len) {
                int pamafive = (len - SIZE) / 2;
                stringBuilder.append(value, 0, pamafive);
                for (int i = 0; i < SIZE; i++) {
                    stringBuilder.append(SYMBOL);
                }
                stringBuilder.append(value, len - (pamafive + 1), len);
            } else {
                int pamafour = len - 2;
                stringBuilder.append(value.charAt(0));
                for (int i = 0; i < pamafour; i++) {
                    stringBuilder.append(SYMBOL);
                }
                stringBuilder.append(value.charAt(len - 1));
            }
        }
        return stringBuilder.toString();
    }

}
