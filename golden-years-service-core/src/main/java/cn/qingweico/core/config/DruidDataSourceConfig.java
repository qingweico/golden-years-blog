package cn.qingweico.core.config;

import cn.qingweico.core.config.split.DynamicDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
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
public class DruidDataSourceConfig {

    private DruidConstant druidConstant;

    private static final String CONFIG_LOCATION = "classpath:mybatis-config.xml";
    private static final String MAPPER_LOCATION = "classpath:mapper/*.xml";
    private static final String TYPE_ALIASES_PACKAGE = "cn.qingweico.entity";


    @Autowired
    public void setDruidConstant(DruidConstant druidConstant) {
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
    public DynamicDataSource dynamicDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                               @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> map = new HashMap<>(2);
        // 与DynamicDataSourceHolder中的DB_MASTER, DB_SLAVE保持一致
        map.put("master", masterDataSource);
        map.put("slave", slaveDataSource);
        dynamicDataSource.setTargetDataSources(map);
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        return dynamicDataSource;
    }

    /**
     * 在生成语句时才决定数据源(懒加载)
     *
     * @param dynamicDataSource {@link DynamicDataSource}
     * @return {@link LazyConnectionDataSourceProxy}
     */
    @Bean
    public LazyConnectionDataSourceProxy dataSource(@Qualifier("dynamicDataSource") DataSource dynamicDataSource) {
        LazyConnectionDataSourceProxy dataSourceProxy = new LazyConnectionDataSourceProxy();
        dataSourceProxy.setTargetDataSource(dynamicDataSource);
        return dataSourceProxy;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        // 不要使用原生的SqlSessionFactoryBean
        // fix 20230930
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource(CONFIG_LOCATION));
        sqlSessionFactoryBean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        GlobalConfig gc = new GlobalConfig();
        gc.setBanner(false);
        sqlSessionFactoryBean.setGlobalConfig(gc);
        return sqlSessionFactoryBean.getObject();
    }


    /**
     * 用于创建事务管理器对象
     *
     * @return PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager createTransactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        Map<String, String> maps = new HashMap<>(5);
        maps.put("loginUsername", "admin");
        maps.put("loginPassword", "admin");
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
