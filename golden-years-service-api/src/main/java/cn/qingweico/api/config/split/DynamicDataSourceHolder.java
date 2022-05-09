package cn.qingweico.api.config.split;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zqw
 * @date 2022/4/4
 */
@Slf4j
public class DynamicDataSourceHolder {
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    public static final String DB_MASTER = "master";
    public static final String DB_SLAVE = "slave";

    public static String getDbType() {
        String db = CONTEXT_HOLDER.get();
        if (db == null) {
            db = DB_MASTER;
        }
        return db;
    }

    public static void setDbType(String dbType) {
        log.info("所使用的数据源是:" + dbType);
        CONTEXT_HOLDER.set(dbType);
    }

    public static void clearDbType() {
        CONTEXT_HOLDER.remove();
    }
}
