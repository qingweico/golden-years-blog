package cn.qingweico.core.config;

import cn.qingweico.core.config.split.DynamicDataSource;
import cn.qingweico.core.config.split.DynamicDataSourceHolder;
import cn.qingweico.core.config.split.DynamicDataSourceInterceptor;
import cn.qingweico.global.DataAccessConstant;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zqw
 * @date 2022/4/4
 * @see com.alibaba.druid.wall.WallConfig
 * @see com.alibaba.druid.pool.DruidAbstractDataSource
 */
@Configuration
@EnableTransactionManagement
public class DruidDataSourceConfig {


    private static final String CONFIG_LOCATION = "classpath:mybatis-config.xml";
    private static final String MAPPER_LOCATION = "classpath:mapper/*.xml";
    private static final String TYPE_ALIASES_PACKAGE = "cn.qingweico.entity";


    @ConfigurationProperties(prefix = "spring.datasource.druid.master")
    @Bean(name = "masterDataSource")
    @Qualifier("masterDataSource")
    public DataSource masterDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @ConfigurationProperties(prefix = "spring.datasource.druid.slave")
    @Bean(name = "slaveDataSource")
    @Qualifier("slaveDataSource")
    public DataSource slaveDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @DependsOn({"masterDataSource", "slaveDataSource"})
    @Primary
    @Bean
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> map = new HashMap<>(2);
        // 与DynamicDataSourceHolder中的DB_MASTER, DB_SLAVE保持一致
        map.put(DynamicDataSourceHolder.DB_MASTER, masterDataSource());
        map.put(DynamicDataSourceHolder.DB_SLAVE, slaveDataSource());
        dynamicDataSource.setTargetDataSources(map);
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource());
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
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DynamicDataSource dataSource) throws Exception {
        // 不要使用原生的SqlSessionFactoryBean
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
    public PlatformTransactionManager createTransactionManager(@Qualifier("dynamicDataSource") DynamicDataSource dataSource) {
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
