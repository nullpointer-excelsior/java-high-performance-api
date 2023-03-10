package com.benjamin.stream;

import com.benjamin.products.application.ProductUseCases;
import com.benjamin.products.domain.Product;
import com.benjamin.products.infrastructure.spring.ProductConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Log4j2
@RestController
@RequestMapping("product")
@SpringBootApplication
@Import(ProductConfiguration.class)
public class WebFluxApplication {

    @Autowired
    private ProductUseCases products;

    public static void main(String[] args) {
        SpringApplication.run(WebFluxApplication.class, args);
    }

    @GetMapping
    public Flux<Product> getAllProducts() {
        log.info("All products request");
        return Flux.fromStream(products.getAllProducts());
    }

    @GetMapping("no-stock/count")
    public Mono<Long> countSkuWithoutStock() {
        log.info("no-stock count request");
        return Mono.just(products.countSkuWithoutStock());
    }

    @GetMapping("no-stock")
    public Flux<Product> getProductWithoutStock() {
        log.info("no-stock products request");
       return Flux.fromStream(products.getSkuWithoutStock())
               .retryWhen(Retry.fixedDelay(100, Duration.ofSeconds(5)));
    }

}
