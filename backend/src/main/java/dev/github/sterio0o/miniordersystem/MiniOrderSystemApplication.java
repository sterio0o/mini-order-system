package dev.github.sterio0o.miniordersystem;

import dev.github.sterio0o.common.config.CommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CommonConfig.class)
public class MiniOrderSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniOrderSystemApplication.class, args);
    }

}
