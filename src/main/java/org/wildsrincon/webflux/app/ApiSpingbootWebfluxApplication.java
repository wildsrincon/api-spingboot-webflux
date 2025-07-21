package org.wildsrincon.webflux.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.wildsrincon.webflux.app.model.documents.Category;
import org.wildsrincon.webflux.app.model.documents.Product;
import org.wildsrincon.webflux.app.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@SpringBootApplication
public class ApiSpingbootWebfluxApplication implements CommandLineRunner {

    @Autowired
    private ProductService service;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private static final Logger log = LoggerFactory.getLogger(ApiSpingbootWebfluxApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiSpingbootWebfluxApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // Verificar si ya existen categorías en la base de datos
        service.findAllCategories()
                .hasElements()
                .flatMap(hasCategories -> {
                    if (!hasCategories) {
                        log.info("No se encontraron categorías. Creando datos iniciales...");
                        return createInitialData();
                    } else {
                        log.info("Las categorías ya existen. Omitiendo creación de datos iniciales.");
                        return Mono.empty();
                    }
                })
                .subscribe();
    }

    private Mono<Void> createInitialData() {
        // Solo limpiar productos, mantener categorías existentes
        return mongoTemplate.dropCollection("products")
                .then(createCategoriesAndProducts());
    }

    private Mono<Void> createCategoriesAndProducts() {
        Category electronics = new Category("Electronics");
        Category computers = new Category("Computers");
        Category smartphones = new Category("Smartphones");
        Category tablets = new Category("Tablets");
        Category wearables = new Category("Wearables");

        // Guardar categorías en la base de datos
        return Flux.just(electronics, computers, smartphones, tablets, wearables)
                .flatMap(service::saveCategory)
                .doOnNext(category -> {
                    log.info("Categoría guardada: {} - {}", category.getId(), category.getName());
                })
                .collectList()
                .flatMap(savedCategories -> {
                    // Crear productos usando las categorías guardadas
                    return Flux.just(
                                    new Product("Tv Samsung", "Televisor 4K UHD", 499.99, savedCategories.get(0)),
                                    new Product("Laptop Dell", "Portátil con 16GB RAM", 899.99, savedCategories.get(1)),
                                    new Product("Smartphone Xiaomi", "Teléfono con cámara de 108MP", 299.99, savedCategories.get(2)),
                                    new Product("Tablet Apple", "iPad Pro con M1", 1099.99, savedCategories.get(3)),
                                    new Product("Smartwatch Garmin", "Reloj inteligente con GPS", 349.99, savedCategories.get(4)),
                                    new Product("Monitor LG", "Pantalla 27 pulgadas 4K", 399.99, savedCategories.get(0)),
                                    new Product("Auriculares Sony", "Auriculares inalámbricos con cancelación de ruido", 199.99, savedCategories.get(0)),
                                    new Product("Teclado Mecánico Corsair", "Teclado mecánico retroiluminado", 129.99, savedCategories.get(1))
                            )
                            .flatMap(product -> {
                                product.setCreateAt(new Date());
                                return service.saveProduct(product);
                            })
                            .doOnNext(product -> log.info("Producto guardado: {} - {} - {} - {}",
                                    product.getId(), product.getName(), product.getDescription(), product.getPrice()))
                            .then();
                });
    }
}