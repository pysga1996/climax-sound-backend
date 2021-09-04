package com.alpha.config.general;

import lombok.extern.log4j.Log4j2;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author thanhvt
 * @created 26/07/2021 - 1:02 SA
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public TaskExecutor getAsyncExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Exception message - " + ex.getMessage());
            log.error("Method name - " + method.getName());
            for (Object param : params) {
                log.error("Parameter value - " + param);
            }
        };
    }
}
