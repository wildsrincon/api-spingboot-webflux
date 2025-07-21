package org.wildsrincon.webflux.app.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import org.wildsrincon.webflux.app.model.documents.Category;
import org.wildsrincon.webflux.app.model.documents.Product;
import org.wildsrincon.webflux.app.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SessionAttributes("product")
@Controller
public class ProductController {
    @Autowired
    private ProductService service;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @ModelAttribute("categories")
    public Flux<Category> categories() {
        return service.findAllCategories();
    } ;

    // Add methods to handle product-related requests here
    // For example, you can add methods to get products, create a new product, etc.

    // Get all products
    @GetMapping({"/products", "/"})
    public Mono<String> getAllProducts(Model model) {
        Flux<Product> products = service.findAll();

        model.addAttribute("products", products);
        model.addAttribute("title", "Products List");

        return Mono.just("products"); // This should match the name of your Thymeleaf template
    }

    // Create a new product
    @GetMapping("/form")
    public Mono<String> createProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("title", "Form Create Product");
        return Mono.just("form"); // This should match the name of your Thymeleaf template
    }

    // Edit an existing product
    @GetMapping("/form/{id}")
    public Mono<String> updateProduct(@PathVariable String id, Model model) {
        Mono<Product> productMono = service.findById(id).doOnNext(prod -> {
            log.info("Product found: {} with ID: {}", prod.getName(), prod.getId());
        }).defaultIfEmpty(new Product());

        model.addAttribute("title", "Form Edit Product");
        model.addAttribute("button", "Edit");
        model.addAttribute("product", productMono);

        return Mono.just("form");
    }

    @PostMapping("/form")
    public Mono<String> saveProduct(@ModelAttribute @Valid Product product, BindingResult result, Model model,  SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("title", "Errors In Form Create Product");
            model.addAttribute("button", "Save");
            return Mono.just("form");
        } else {
            status.setComplete();

            Mono<Category> categoryMono = service.findCategoryById(product.getCategory().getId());
            return categoryMono.flatMap(category -> {
                if (product.getCreateAt() == null) {
                    product.setCreateAt(new java.util.Date());
                }
                product.setCategory(category);
                return service.saveProduct(product);
            }).doOnNext(prod -> {
                    log.info("Category assigned: {} with ID: {}", prod.getCategory().getName(), prod.getCategory().getId());
                    log.info("Product saved: {} with ID: {}", prod.getName(), prod.getId());
            }).thenReturn("redirect:/products");
        }
    }

    // Delete a product
    @GetMapping("/delete/{id}")
    public Mono<String> deleteProduct(@PathVariable String id) {
        return service.findById(id)
            .defaultIfEmpty(new Product())
            .flatMap(product -> {
                if (product.getId() == null) {
                    return Mono.error(new InterruptedException("Product not found"));
                }
                return Mono.just(product);
            })
            .flatMap(product -> {
                log.info("Product to delete: {} with ID: {}", product.getName(), product.getId());
                return service.delete(product);
            }).thenReturn("redirect:/products");
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

