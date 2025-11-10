//package com.ashtana.backend.Service;
//
//import com.ashtana.backend.Entity.Category;
//import com.ashtana.backend.Entity.Product;
//import com.ashtana.backend.Repository.ProductRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ProductServiceOld {
//    private final ProductRepository productRepository;
//
//    public ProductServiceOld(ProductRepository productRepository) {
//        this.productRepository = productRepository;
//    }
//
//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }
//
//    public List<Product> getProductsByCategory(Category category) {
//        return productRepository.findByCategory(category);
//    }
//
//    public Product addProduct(Product product) {
//        return productRepository.save(product);
//    }
//}
