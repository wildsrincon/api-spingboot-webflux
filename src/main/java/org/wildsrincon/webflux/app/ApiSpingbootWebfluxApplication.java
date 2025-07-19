package org.wildsrincon.webflux.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.wildsrincon.webflux.app.repository.ProductDAO;
import org.wildsrincon.webflux.app.model.documents.Product;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class ApiSpingbootWebfluxApplication implements CommandLineRunner {

    /**
     * Main method to run the Spring Boot application.
     *
     */

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private static final Logger log = LoggerFactory.getLogger(ApiSpingbootWebfluxApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiSpingbootWebfluxApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // Clear the collection before inserting new products
        mongoTemplate.dropCollection("products").subscribe();

        Flux.just(
                new Product("Tv Samsung", 499.99),
                new Product("Laptop Dell", 899.99),
                new Product("Smartphone Xiaomi",  299.99),
                new Product("Tablet Apple", 399.99),
                new Product("Smartwatch Garmin", 199.99),
                new Product("Monitor LG", 249.99),
                new Product("Auriculares Sony",149.99),
                new Product("Teclado Mecánico Corsair",129.99)
        )
                .flatMap(product ->  {
                    // Set the date to the current time
                    product.setCreateAt(new Date());
                    return productDAO.save(product);
                })
                .subscribe(product -> log.info("{} - {} - {}", product.getId(), product.getName(), product.getPrice()));

    }
}
