package dev.github.sterio0o.paymentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class PaymentGateway {

    private final Random random = new Random();

    public boolean process() {
        try {
            Thread.sleep(2000);
            int randomInt = random.nextInt(101);
            return randomInt <= 80;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
