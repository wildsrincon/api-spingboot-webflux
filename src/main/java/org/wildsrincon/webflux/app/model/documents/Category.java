package org.wildsrincon.webflux.app.model.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "categories")
public class Category {
    @Id
    private String id;

    private String name;

    // Default constructor
    public Category() {
    }

    // Constructor with name
    public Category(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
