package cn.qingweico.core.config;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author zqw
 * @date 2022/12/25
 */
@Slf4j
@Configuration
public class ConnectionLogFilter extends FilterEventAdapter {

    @Override
    public void connection_connectBefore(FilterChain chain, Properties info) {
        log.info("druid connect before!");
    }

    @Override
    public void connection_connectAfter(ConnectionProxy connection) {
        log.info("druid connect after!");
    }
}
