package dev.github.sterio0o.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "dev.github.sterio0o.common")
@Import({AopAutoConfig.class, SecurityConfiguration.class})
public class CommonConfig {
}
