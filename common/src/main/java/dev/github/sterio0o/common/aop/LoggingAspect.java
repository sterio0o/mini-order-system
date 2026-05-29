package dev.github.sterio0o.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    // Pointcut
    @Pointcut("@within(org.springframework.stereotype.Controller)")
    public void isControllerLayer() {}

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void isServiceLayer() {}

    // Advice
    @Around("isControllerLayer()")
    public Object loggingControllerMethod(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toString();
        log.debug("Входящий запрос: {}", methodName);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis() - start;

            log.debug("Ответ: {} ({}ms)", methodName, end);

            return result;
        } catch (Throwable e) {
            log.error("Ошибка: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Around("isServiceLayer()")
    public Object loggingServiceMethod(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toString();
        log.debug("Вызов метода {} с аргументами: {}", methodName, joinPoint.getArgs());
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis() - start;
            log.debug("Метод {} вернул результат: {} ({}ms)", methodName, result, end);

            return result;
        } catch (Throwable e) {
            log.error("Метод {} завершил работу с ошибкой: {}", methodName, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
