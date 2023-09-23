package cn.qingweico.util;

/**
 * @author zqw
 * @date 2023/9/23
 */
public class CollUtils {
    /**
     * @param expectedSize map size
     * @return int
     */
    public static int mapSize(int expectedSize) {
        return (int) ((float) expectedSize / 0.75F + 1.0F);
    }
}
