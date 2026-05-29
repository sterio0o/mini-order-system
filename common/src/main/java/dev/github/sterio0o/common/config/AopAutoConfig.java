package dev.github.sterio0o.common.config;

import dev.github.sterio0o.common.aop.LoggingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AopAutoConfig {

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }

}
