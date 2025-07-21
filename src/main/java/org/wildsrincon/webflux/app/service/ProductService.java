package org.wildsrincon.webflux.app.service;

import org.wildsrincon.webflux.app.model.documents.Category;
import org.wildsrincon.webflux.app.model.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    /**
     * Interface for Product Service
     * Provides methods to interact with Product data
     */

    // Method signatures for CRUD operations on Product entities

    // Retrieves all products
    Flux<Product> findAll();

    // Retrieves all products with a name in uppercase
    Flux<Product> findAllWithUpper();

    // Retrieves a product repeat 5000 times with a name in uppercase
    Flux<Product> findAllWithUpperRepeat();

    // Retrieves a product by its ID
    Mono<Product> findById(String id);

    // Saves a new product
    Mono<Product> saveProduct(Product product);

    // Updates an existing product by ID
    Mono<Product> update(Product product);

    // Delete Product
   Mono<Void> delete(Product product);

    // Retrieves all categories
    Flux<Category> findAllCategories();

    // Retrieves a category by its ID
    Mono<Category> findCategoryById(String id);

    // Saves a new category
    Mono<Category> saveCategory(Category category);

}
