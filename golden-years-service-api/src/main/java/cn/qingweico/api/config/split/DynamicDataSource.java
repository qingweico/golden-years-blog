package cn.qingweico.api.config.split;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * mysql主从复制
 *
 * @author zqw
 * @date 2022/4/4
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceHolder.getDbType();
    }
}
