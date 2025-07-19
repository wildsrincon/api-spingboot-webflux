package org.wildsrincon.webflux.app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wildsrincon.webflux.app.modules.DAO.ProductDAO;
import org.wildsrincon.webflux.app.modules.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {
    @Autowired
    private ProductDAO productDAO;

    private static final Logger log = LoggerFactory.getLogger(ProductRestController.class);

    // Get all products as a REST endpoint
    @GetMapping
    public Flux<Product> index() {
        return productDAO.findAll()
            .map(product -> {
                product.setName(product.getName().toUpperCase());
                return product;
            })
            .doOnNext(prod -> log.info("Product Upper: {}", prod.getName())); // Return the Flux of products directly
    }

    // Get product by ID as a REST endpoint
    @GetMapping("/{id}")
    public Mono<Product> show( @PathVariable String id) {
        Flux<Product> products = productDAO.findAll();
        Mono<Product> productMono = products
            .filter(product -> product.getId().equals(id))
            .next()
            .doOnNext(prod -> log.info("Product Upper: {}", prod.getName()));

        return productMono.switchIfEmpty(Mono.error(new RuntimeException("Product not found with ID: " + id)));
    }
}

