package cn.qingweico.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * 0: 符号位
 * 1 ~ 41: 时间戳
 * 42 ~ 51: 机器id
 * 52 ~ 63: 序列号
 *
 * @author zqw
 * @date 2022/2/25
 */
public class SnowflakeIdWorker {
    private static long workId;
    private static long sequence = 0L;
    private static final long SERVICE_ID = Math.abs(System.getenv().hashCode()) % 32;
    private static long lastTime = System.currentTimeMillis();
    private static final long MAX_SEQUENCE = (1 << 12) - 1;

    static {
        try {
            workId = Math.abs(Inet4Address.getLocalHost().getHostAddress().hashCode()) % 32;
        } catch (UnknownHostException e) {
            workId = new Random().nextLong() % 32;
        }
    }

    private SnowflakeIdWorker() {
    }

    public synchronized static String nextId() {
        long l = System.currentTimeMillis();
        if (lastTime == l) {
            ++sequence;
        } else {
            lastTime = l;
            sequence = 0;
        }
        if (sequence > MAX_SEQUENCE) {
            nextId();
        }
        return String.valueOf(lastTime << 22 | SERVICE_ID << 17 | workId << 12 | sequence);
    }
}
