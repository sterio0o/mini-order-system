package dev.github.sterio0o.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // Сколько потоков будут работать всегда
        taskExecutor.setCorePoolSize(4);
        // Количество потоков при пиковой нагрузке
        taskExecutor.setMaxPoolSize(8);
        // Размер очереди задач, если все потоки заняты, то задачи кладутся сюда
        taskExecutor.setQueueCapacity(100);
        // Имя потока в логах
        taskExecutor.setThreadNamePrefix("GeneratorThread-");
        // Что делать если пулл переполнен? Выполнить в вызывающем потоке
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        taskExecutor.initialize();

        // передача security контекста в async поток
        return new DelegatingSecurityContextAsyncTaskExecutor(taskExecutor);
    }

}
