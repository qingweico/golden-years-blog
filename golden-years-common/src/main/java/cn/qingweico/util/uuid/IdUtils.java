package cn.qingweico.util.uuid;

import java.util.concurrent.ThreadLocalRandom;

/**
 * ID生成器工具类
 *
 * @author zqw
 */
public class IdUtils
{
    /**
     * 获取随机UUID
     *
     * @return 随机UUID
     */
    public static String randomUuid()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID 去掉了横线
     *
     * @return 简化的UUID 去掉了横线
     */
    public static String simpleUuid()
    {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID 使用性能更好的 {@link ThreadLocalRandom} 生成UUID
     *
     * @return 随机UUID
     */
    public static String fastUuid()
    {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID 去掉了横线 使使用性能更好的 {@link ThreadLocalRandom} 生成UUID
     *
     * @return 简化的UUID 去掉了横线
     */
    public static String fastSimpleUuid()
    {
        return UUID.fastUUID().toString(true);
    }
}
