package dev.github.sterio0o.paymentservice;

import dev.github.sterio0o.common.config.CommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@Import(CommonConfig.class)
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

}
