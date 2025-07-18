package org.wildsrincon.webflux.app.modules.DAO;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.wildsrincon.webflux.app.modules.documents.Product;

public interface ProductDAO extends ReactiveMongoRepository<Product,String> {

}
