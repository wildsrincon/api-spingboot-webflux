package org.wildsrincon.webflux.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.wildsrincon.webflux.app.modules.DAO.ProductDAO;
import org.wildsrincon.webflux.app.modules.documents.Product;
import reactor.core.publisher.Flux;

@Controller
public class ProductController {
    @Autowired
    private ProductDAO productDAO;

    // Add methods to handle product-related requests here
    // For example, you can add methods to get products, create a new product, etc.

    // Get all products
    @GetMapping({"/products", "/"})
    public String getAllProducts(Model model) {
        Flux<Product> products = productDAO.findAll();
        model.addAttribute("products", products);
        model.addAttribute("title", "Products List");

        return "products"; // This should match the name of your Thymeleaf template
    }
}

