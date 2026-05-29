package dev.github.sterio0o.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AopAutoConfig.class, SecurityConfiguration.class})
public class CommonConfig {
}
