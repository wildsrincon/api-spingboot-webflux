package org.wildsrincon.webflux.app.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.wildsrincon.webflux.app.model.documents.Category;

public interface CategoryDAO extends ReactiveMongoRepository<Category, String> {

}
