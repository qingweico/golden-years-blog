package cn.qingweico.core.config.split;

import cn.qingweico.entity.model.LoginUser;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.SecurityUtils;
import cn.qingweico.util.StringUtils;
import cn.qingweico.util.clazz.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Locale;


/**
 * mysql主从复制
 *
 * @author zqw
 * @date 2022/4/4
 */

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class,
                        RowBounds.class, ResultHandler.class,
                        CacheKey.class, BoundSql.class})})
@Slf4j
public class DynamicDataSourceInterceptor implements Interceptor {
    private static final String REGEX = ".*insert\\u0020.*|.*update\\0020.*|.*delete\\0020.*";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String sqlId = mappedStatement.getId();
        log.debug("------sqlId------" + sqlId);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];
        log.debug("------sqlCommandType------" + sqlCommandType);
        if (parameter == null) {
            return invocation.proceed();
        }
        // 自动注入创建人、创建时间、修改人、修改时间
        if (SqlCommandType.INSERT == sqlCommandType) {
            Field[] fields = ClassUtils.getAllFields(parameter);
            for (Field field : fields) {
                log.debug("------field.name------" + field.getName());
                try {
                    if ("CREATE_BY".equals(field.getName())) {
                        LoginUser sysUser = SecurityUtils.getLoginUser();
                        field.setAccessible(true);
                        Object createBy = field.get(parameter);
                        field.setAccessible(false);
                        if (createBy == null || StringUtils.isEmpty((String) createBy)) {
                            createBy = "admin";
                            if (sysUser != null) {
                                createBy = sysUser.getUsername();
                            }
                            if (ObjectUtils.isNotEmpty(createBy)) {
                                field.setAccessible(true);
                                field.set(parameter, createBy);
                                field.setAccessible(false);
                            }
                        }
                    }
                    // 注入创建时间
                    if ("CREATE_TIME".equals(field.getName())) {
                        field.setAccessible(true);
                        Object createTime = field.get(parameter);
                        field.setAccessible(false);
                        if (ObjectUtils.isEmpty(createTime)) {
                            field.setAccessible(true);
                            log.info(field.getGenericType().getTypeName());
                            if (field.getType().isAssignableFrom(String.class)) {
                                field.set(parameter, DateUtils.getNowTime());
                            } else {
                                field.set(parameter, new Date());
                            }
                            field.setAccessible(false);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        if (SqlCommandType.UPDATE == sqlCommandType) {
            Field[] fields = ClassUtils.getAllFields(parameter);
            for (Field field : fields) {
                log.debug("------field.name------" + field.getName());
                try {
                    if ("UPDATE_BY".equals(field.getName())) {
                        field.setAccessible(true);
                        Object updateBy = field.get(parameter);
                        field.setAccessible(false);
                        if (ObjectUtils.isEmpty(updateBy)) {
                            updateBy = "admin";
                            // 获取登录用户信息
                            LoginUser sysUser = SecurityUtils.getLoginUser();
                            if (sysUser != null) {
                                // 登录账号
                                updateBy = sysUser.getUsername();
                            }
                            if (ObjectUtils.isNotEmpty(updateBy)) {
                                field.setAccessible(true);
                                field.set(parameter, updateBy);
                                field.setAccessible(false);
                            }
                        }
                    }
                    if ("UPDATE_TIME".equals(field.getName())) {
                        field.setAccessible(true);
                        Object updateTime = field.get(parameter);
                        field.setAccessible(false);
                        if (ObjectUtils.isEmpty(updateTime)) {
                            field.setAccessible(true);
                            field.set(parameter, new Date());
                            field.setAccessible(false);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        Object[] objects = invocation.getArgs();
        String lookupKey = DynamicDataSourceHolder.DB_MASTER;
        if (!synchronizationActive) {
            if (sqlCommandType.equals(SqlCommandType.SELECT)) {
                if (mappedStatement.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
                    lookupKey = DynamicDataSourceHolder.DB_MASTER;
                } else {
                    BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(objects[1]);
                    String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("\\t\\n\\r", " ");
                    if (sql.matches(REGEX)) {
                        lookupKey = DynamicDataSourceHolder.DB_MASTER;
                    } else {
                        lookupKey = DynamicDataSourceHolder.DB_SLAVE;
                    }
                }
            }
        } else {
            lookupKey = DynamicDataSourceHolder.DB_MASTER;
        }
        log.info("设置[{}] use[{}] strategy,SqlCommandType[{}]......", mappedStatement.getId(), lookupKey,
                mappedStatement.getSqlCommandType().name());
        DynamicDataSourceHolder.setDbType(lookupKey);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }
}
