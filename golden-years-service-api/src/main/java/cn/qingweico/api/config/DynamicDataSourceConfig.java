package cn.qingweico.api.config;

import cn.qingweico.api.config.split.DynamicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zqw
 * @date 2022/4/4
 */
@Configuration
public class DynamicDataSourceConfig {
    @Bean
    public DynamicDataSource dynamicDataSource(DataSource masterDataSource, DataSource slaveDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> map = new HashMap<>(2);
        map.put("master", masterDataSource);
        map.put("slave", slaveDataSource);
        dynamicDataSource.setTargetDataSources(map);
        return dynamicDataSource;
    }
}
