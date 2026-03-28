package dev.github.sterio0o.orderservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class KafkaConfig {

    @Bean
    public NewTopic orderCreatedEventTopic() {
        log.info("create New Topic: order-created-event");
        return new NewTopic("order-created-event", 3, (short) 1);
    }

}
