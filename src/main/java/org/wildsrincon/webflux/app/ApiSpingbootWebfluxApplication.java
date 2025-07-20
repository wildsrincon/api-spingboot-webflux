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
                new Product("Tv Samsung", "Televisor 4K UHD", 499.99),
                new Product("Laptop Dell", "Portátil con 16GB RAM", 899.99),
                new Product("Smartphone Xiaomi", "Teléfono con cámara de 108MP", 299.99),
                new Product("Tablet Apple", "iPad Pro con M1", 1099.99),
                new Product("Smartwatch Garmin", "Reloj inteligente con GPS", 349.99),
                new Product("Monitor LG", "Pantalla 27 pulgadas 4K", 399.99),
                new Product("Auriculares Sony", "Auriculares inalámbricos con cancelación de ruido", 199.99),
                new Product("Teclado Mecánico Corsair", "Teclado mecánico retroiluminado", 129.99)
        )
                .flatMap(product ->  {
                    // Set the date to the current time
                    product.setCreateAt(new Date());
                    return productDAO.save(product);
                })
                .subscribe(product -> log.info("{} - {} - {} - {}", product.getId(), product.getName(), product.getDescription(), product.getPrice()));

    }
}
