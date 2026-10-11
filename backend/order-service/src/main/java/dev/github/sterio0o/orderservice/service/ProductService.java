package dev.github.sterio0o.orderservice.service;

import dev.github.sterio0o.orderservice.exception.light.LightProductNotFoundException;
import dev.github.sterio0o.orderservice.model.dto.product.ProductRequestDto;
import dev.github.sterio0o.orderservice.model.dto.product.ProductResponseDto;
import dev.github.sterio0o.orderservice.model.entities.Product;
import dev.github.sterio0o.orderservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final static String CACHE_PREFIX = "products:cache:";
    private final static String COUNTER_PREFIX = "products:counter:";

    private final static Integer NEED_COUNT_TO_CACHE = 3;

    public ProductResponseDto findProductInHotCache(String name) {
        String cacheKey = CACHE_PREFIX + name;
        String counterKey = COUNTER_PREFIX + name;

        ProductResponseDto product = (ProductResponseDto) redisTemplate.opsForValue().get(cacheKey);
        if (product != null) {
            log.info("Product {} найден в кеше", name);
            return product;
        }

        Product productDb = productRepository.findByProductName(name)
                .orElseThrow(() -> new LightProductNotFoundException(
                        "Product with name=" + name + " not found")
                );
        log.info("Product {} взят из БД", name);

        product = ProductResponseDto.fromEntity(productDb);

        long currentRequest = redisTemplate.opsForValue().increment(counterKey);
        log.info("Счетчик для Product {} = {}", name, currentRequest);

        // Если за 5 минут не будет нужное кол-во запросов, то значит товар не популярный
        if (currentRequest == 1)
            redisTemplate.expire(counterKey, Duration.ofMinutes(5));

        if (currentRequest >= NEED_COUNT_TO_CACHE) {
            log.info("Product {} преодолел необходимый порог = {} и помещен в горячий кеш", name, NEED_COUNT_TO_CACHE);
            // Сохраняем в горячий с кеш с TTL 15 минут
            redisTemplate.opsForValue().setIfAbsent(cacheKey, product, Duration.ofMinutes(15));

            //Обнуляем счетчик
            redisTemplate.delete(counterKey);
        }

        return product;
    }

    public Page<ProductResponseDto> findAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(ProductResponseDto::fromEntity);
    }

    @Transactional
    public ProductResponseDto updateProductHotCache(ProductRequestDto updateDto) {
        Product product = productRepository.findById(updateDto.id())
                .orElseThrow(() -> new LightProductNotFoundException(
                        "Product with ID=" + updateDto.id() + " not found")
                );

        // Запоминаем старое имя на случай, если оно будет изменено
        String prevName = product.getProductName();

        product.setProductName(updateDto.productName());
        product.setPrice(updateDto.price());

        Product saved = productRepository.save(product);

        // Очистка кеша для консистентности данных
        redisTemplate.delete(CACHE_PREFIX + prevName);
        redisTemplate.delete(COUNTER_PREFIX + prevName);

        log.info("Данные Product {} успешно изменены и кеш очищен", prevName);

        return ProductResponseDto.fromEntity(saved);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new LightProductNotFoundException(
                        "Product with ID=" + id + " not found")
                );

        // Очищаем кеш перед удалением
        redisTemplate.delete(CACHE_PREFIX + product.getProductName());
        redisTemplate.delete(COUNTER_PREFIX + product.getProductName());

        log.info("Product {} удален из кеша и из БД", product.getProductName());

        productRepository.deleteById(id);
    }

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = new Product();
        product.setProductName(requestDto.productName());
        product.setPrice(requestDto.price());

        Product saved = productRepository.save(product);
        return ProductResponseDto.fromEntity(saved);
    }
}
