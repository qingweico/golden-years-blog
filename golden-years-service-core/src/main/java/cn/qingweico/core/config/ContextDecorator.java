package cn.qingweico.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Nonnull;

/**
 * @author zqw
 * @date 2022/3/30
 */
@Configuration
public class ContextDecorator implements TaskDecorator {
    @Override
    @Nonnull
    public Runnable decorate(@Nonnull Runnable runnable) {
        RequestAttributes context = RequestContextHolder.currentRequestAttributes();
        return () -> {
            try {
                RequestContextHolder.setRequestAttributes(context);
                runnable.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }
}
