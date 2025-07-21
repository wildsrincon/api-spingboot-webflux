package org.wildsrincon.webflux.app.model.documents;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document( collection = "products")
public class Product {
    @Id
    private String id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    private Double price;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;

    @NotNull
    @Valid
    private Category category;

    private String image;

    // Default constructor
    public Product() {
    }

    // Constructors
    public Product(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Product(String name, String description, Double price, Category category) {
        this(name, description, price);
        this.category = category;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Date getCreateAt() {
        return createAt;
    }
    public void setCreateAt(Date createAt) { this.createAt = createAt; }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    // Getter and Setter for Category
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

}

