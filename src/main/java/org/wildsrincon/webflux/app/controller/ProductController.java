package org.wildsrincon.webflux.app.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
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

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

@SessionAttributes("product")
@Controller
public class ProductController {
    @Autowired
    private ProductService service;

    @Value("${config.uploads.path}")
    private String pathUploads;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @ModelAttribute("categories")
    public Flux<Category> categories() {
        return service.findAllCategories();
    } ;

    // Add methods to handle product-related requests here
    // For example, you can add methods to get products, create a new product, etc.

    // Crear directorio de uploads si no existe
    private void createUploadsDirectoryIfNotExists() {
        File uploadsDir = new File(pathUploads);
        if (!uploadsDir.exists()) {
            boolean created = uploadsDir.mkdirs();
            if (created) {
                log.info("Directorio de uploads creado: {}", uploadsDir.getAbsolutePath());
            } else {
                log.error("No se pudo crear el directorio: {}", uploadsDir.getAbsolutePath());
            }
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return UUID.randomUUID().toString() + ".jpg";
        }

        String cleanFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        String extension = "";
        int dotIndex = cleanFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = cleanFilename.substring(dotIndex);
            cleanFilename = cleanFilename.substring(0, dotIndex);
        }

        return UUID.randomUUID().toString() + "-" + cleanFilename + extension;
    }

    // View image of a product
    @GetMapping("/uploads/img/{filename:.+}")
    public Mono<ResponseEntity<Resource>> viewImage(@PathVariable String filename, Model model) throws MalformedURLException {
        Path filePath = Paths.get(pathUploads).resolve(filename).toAbsolutePath();

        Resource image = new UrlResource(filePath.toUri());

        return Mono.just(ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFilename() + "\"")
                .body(image))
                .doOnSuccess(response -> log.info("Image served: {}", filename))
                .doOnError(error -> log.error("Error serving image: ", error));
    }

    // View product details
    @GetMapping("/product-detail/{id}")
    public Mono<String> viewProductDetail(@PathVariable String id, Model model) {
        Mono<Product> productMono = service.findById(id)
                .doOnNext(prod -> log.info("Product found: {} with ID: {}", prod.getName(), prod.getId()))
                .switchIfEmpty(Mono.error(new InterruptedException("Product not found")));

        model.addAttribute("product", productMono);
        model.addAttribute("title", "Product Detail");

        return Mono.just("product-detail"); // This should match the name of your Thymeleaf template
    }

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
    public Mono<String> saveProduct(@ModelAttribute @Valid Product product,
                                    BindingResult result,
                                    Model model,
                                    @RequestPart(required = false) FilePart file,
                                    SessionStatus status) {

        // Validar errores de formulario
        if (result.hasErrors()) {
            model.addAttribute("title", "Errors In Form Create Product");
            model.addAttribute("button", "Save");
            return Mono.just("form");
        }

        // Validar que se haya seleccionado una categoría
        if (product.getCategory() == null || product.getCategory().getId() == null ||
                product.getCategory().getId().trim().isEmpty()) {
            result.rejectValue("category", "category.required", "Debe seleccionar una categoría");
            model.addAttribute("title", "Errors In Form Create Product");
            model.addAttribute("button", "Save");
            return Mono.just("form");
        }

        status.setComplete();

        return service.findCategoryById(product.getCategory().getId())
                .flatMap(category -> {
                    // Establecer fecha si es null
                    if (product.getCreateAt() == null) {
                        product.setCreateAt(new java.util.Date());
                    }

                    product.setCategory(category);

                    // Procesar archivo si existe
                    if (file != null && !file.filename().isEmpty()) {
                        String uniqueFilename = generateUniqueFileName(file.filename());
                        product.setImage(uniqueFilename);
                        log.info("Archivo a guardar: {}", uniqueFilename);
                    }

                    return service.saveProduct(product);
                })
                .flatMap(savedProduct -> {
                    log.info("Product saved: {} with ID: {}", savedProduct.getName(), savedProduct.getId());

                    // Guardar archivo físicamente si existe
                    if (file != null && !file.filename().isEmpty() && savedProduct.getImage() != null) {
                        return saveFileToFileSystem(file, savedProduct.getImage())
                                .thenReturn(savedProduct);
                    }
                    return Mono.just(savedProduct);
                })
                .doOnSuccess(p -> log.info("Proceso completado para producto: {}", p.getName()))
                .doOnError(error -> log.error("Error guardando producto: ", error))
                .thenReturn("redirect:/products")
                .onErrorReturn("form");
    }

    private Mono<Void> saveFileToFileSystem(FilePart filePart, String filename) {
        // Crear directorio si no existe
        createUploadsDirectoryIfNotExists();

        // Crear ruta completa del archivo
        Path filePath = Paths.get(pathUploads).resolve(filename);
        File destFile = filePath.toFile();

        log.info("Guardando archivo en: {}", destFile.getAbsolutePath());

        return filePart.transferTo(destFile)
                .doOnSuccess(v -> log.info("Archivo guardado exitosamente: {}", destFile.getAbsolutePath()))
                .doOnError(error -> log.error("Error guardando archivo: ", error));
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

