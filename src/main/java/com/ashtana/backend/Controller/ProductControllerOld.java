//package com.ashtana.backend.Controller;
//
//import com.ashtana.backend.Entity.Category;
//import com.ashtana.backend.Entity.Product;
//import com.ashtana.backend.Service.ProductServiceOld;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/products")
////@CrossOrigin(origins = "http://localhost:4200") // allow Angular dev server
//public class ProductControllerOld {
//
//    private final ProductServiceOld productService;
//
//    public ProductControllerOld(ProductServiceOld productService) {
//        this.productService = productService;
//    }
//
//    @GetMapping
//    public List<Product> getAllProducts() {
//        return productService.getAllProducts();
//    }
//
//    @GetMapping("/category/{category}")
//    public List<Product> getProductsByCategory(@PathVariable Category category) {
//        return productService.getProductsByCategory(category);
//    }
//
//    @PostMapping
//    public Product addProduct(@RequestBody Product product) {
//        return productService.addProduct(product);
//    }
//}