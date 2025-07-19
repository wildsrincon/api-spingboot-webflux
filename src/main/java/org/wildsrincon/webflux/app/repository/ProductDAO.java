package org.wildsrincon.webflux.app.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.wildsrincon.webflux.app.model.documents.Product;


public interface ProductDAO extends ReactiveMongoRepository<Product,String> {

}
