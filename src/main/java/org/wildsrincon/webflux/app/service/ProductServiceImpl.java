package org.wildsrincon.webflux.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wildsrincon.webflux.app.model.documents.Category;
import org.wildsrincon.webflux.app.model.documents.Product;
import org.wildsrincon.webflux.app.repository.CategoryDAO;
import org.wildsrincon.webflux.app.repository.ProductDAO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

    // Inject the ProductDAO or repository here if needed
    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    // Implement the methods from ProductService interface
    @Override
    public Flux<Product> findAll() {
        return productDAO.findAll();
    }

    @Override
    public Flux<Product> findAllWithUpper() {
        return productDAO.findAll()
        .map(product -> {
            product.setName(product.getName().toUpperCase());
            return product;
        });
    }

    @Override
    public Flux<Product> findAllWithUpperRepeat() {
        return findAllWithUpper().repeat(5000);
    }

    @Override
    public Mono<Product> findById(String id) {
        return productDAO.findById(id);
    }

    @Override
    public Mono<Product> save(Product product) {
        return productDAO.save(product);
    }

    @Override
    public Mono<Product> update(Product product) {
        return productDAO.findById(product.getId())
            .flatMap(existingProduct -> {;
                existingProduct.setName(product.getName());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setDescription(product.getDescription());
                return productDAO.save(existingProduct);
            });
    }

    @Override
    public Mono<Void> delete(Product product) {
        return productDAO.delete(product);
    }

    // Implement the methods from Category interface
    @Override
    public Flux<Category> findAllCategories() {
        return categoryDAO.findAll();
    }

    @Override
    public Mono<Category> findCategoryById(String id) {
        return categoryDAO.findById(id);
    }

    @Override
    public Mono<Category> saveCategory(Category category) {
        return categoryDAO.save(category);
    }


}
