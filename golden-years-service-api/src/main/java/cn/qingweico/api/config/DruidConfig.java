package cn.qingweico.api.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zqw
 * @date 2022/4/4
 */
@Configuration
@EnableTransactionManagement
public class DruidConfig {

    DruidConstant druidConstant;

    @Autowired
    public void setDruidConfig(DruidConstant druidConstant) {
        this.druidConstant = druidConstant;
    }

    @Bean(name = "masterDataSource")
    @Primary
    public DataSource masterDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(druidConstant.getMasterUrl());
        return getDataSource(druidDataSource);
    }

    @Bean(name = "slaveDataSource")
    public DataSource slaveDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(druidConstant.getSlaveUrl());
        return getDataSource(druidDataSource);
    }


    private DataSource getDataSource(DruidDataSource druidDataSource) throws SQLException {
        druidDataSource.setDriverClassName(druidConstant.getDriverClassName());
        druidDataSource.setUsername(druidConstant.getUsername());
        druidDataSource.setPassword(druidConstant.getPassword());
        druidDataSource.setInitialSize(druidConstant.getInitialSize());
        druidDataSource.setMinIdle(druidConstant.getMinIdle());
        druidDataSource.setMaxActive(druidConstant.getMaxActive());
        druidDataSource.setMaxWait(druidConstant.getMaxWait());
        druidDataSource.setTimeBetweenEvictionRunsMillis(druidConstant.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(druidConstant.getMinEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(druidConstant.getValidationQuery());
        druidDataSource.setTestWhileIdle(druidConstant.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(druidConstant.isTestOnBorrow());
        druidDataSource.setTestOnReturn(druidConstant.isTestOnReturn());
        druidDataSource.setPoolPreparedStatements(druidConstant.isPoolPreparedStatements());
        druidDataSource.setFilters(druidConstant.getFilters());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(druidConstant.getMaxPoolPreparedStatementPerConnectionSize());
        druidDataSource.setUseGlobalDataSourceStat(druidConstant.isUseGlobalDataSourceStat());
        druidDataSource.setConnectionProperties(druidConstant.getConnectionProperties());
        return druidDataSource;
    }


    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        Map<String, String> maps = new HashMap<>(5);
        maps.put("loginUsername", "admin");
        maps.put("loginPassword", "990712");
        maps.put("allow", "");
        maps.put("deny", "");
        bean.setInitParameters(maps);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> webStatFilter() {
        FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>(new WebStatFilter());
        bean.setUrlPatterns(Collections.singletonList("/*"));
        Map<String, String> maps = new HashMap<>(2);
        maps.put("exclusion", "*.js/,*.css,/druid/*");
        bean.setInitParameters(maps);
        return bean;
    }
}