package org.wildsrincon.webflux.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import org.wildsrincon.webflux.app.model.documents.Product;
import org.wildsrincon.webflux.app.service.ProductService;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
public class ProductController {
    @Autowired
    private ProductService service;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    // Add methods to handle product-related requests here
    // For example, you can add methods to get products, create a new product, etc.

    // Get all products
    @GetMapping({"/products", "/"})
    public String getAllProducts(Model model) {
        Flux<Product> products = service.findAll();

        products.subscribe(prod -> log.info("Product Upper: {}", prod.getName()));
        model.addAttribute("products", products);
        model.addAttribute("title", "Products List");

        return "products"; // This should match the name of your Thymeleaf template
    }

    // Get all products with backpressure technique
    @GetMapping("/products-datadriver")
    public String getAllProductsDataDriver(Model model) {
        Flux<Product> products = service.findAllWithUpper().delayElements(Duration.ofSeconds(1));

        products.subscribe(prod -> log.info("Product Upper: {}", prod.getName()));
        model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2));
        model.addAttribute("title", "Products List");

        return "products"; // This should match the name of your Thymeleaf template
    }

    // Get all products full mode backpressure technique
    @GetMapping("/products-full")
    public String getAllProductsFull(Model model) {
        Flux<Product> products = service.findAllWithUpperRepeat();

        products.subscribe(prod -> log.info("Product Upper: {}", prod.getName()));
        model.addAttribute("products", products);
        model.addAttribute("title", "Products List");

        return "products"; // This should match the name of your Thymeleaf template
    }

    // Get all products full mode backpressure technique chunked mode
    @GetMapping("/products-chunked")
    public String getAllProductsChunked(Model model) {
        Flux<Product> products = service.findAllWithUpperRepeat();

        products.subscribe(prod -> log.info("Product Upper: {}", prod.getName()));
        model.addAttribute("products", products);
        model.addAttribute("title", "Products List");

        return "products-chunked"; // This should match the name of your Thymeleaf template
    }
}

