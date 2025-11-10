package com.ashtana.backend.Controller;

import com.ashtana.backend.DTO.RequestDTO.ProductRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.ProductResponseDTO;
import com.ashtana.backend.Service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    // -------------------- CREATE PRODUCT ENDPOINTS -------------------- //

    /**
     * CREATE PRODUCT with multiple images (Primary endpoint)
     */
//    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ProductResponseDTO> createProductWithMultipleImages(
//            @Valid @RequestPart("product") ProductRequestDTO productRequest,
//            @RequestPart("images") MultipartFile[] images) {
//
//        log.info("Creating new product: {} with {} images",
//                productRequest.getName(), images != null ? images.length : 0);
//
//        try {
//            ProductResponseDTO response = productService.createProductWithMultipleImages(productRequest, images);
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (IllegalArgumentException e) {
//            log.error("Validation error while creating product: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(null);
//        } catch (Exception e) {
//            log.error("Error creating product: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> createProductWithMultipleImages(
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {

        try {
            // Parse JSON manually
            ObjectMapper objectMapper = new ObjectMapper();
            ProductRequestDTO productRequest = objectMapper.readValue(productJson, ProductRequestDTO.class);

            log.info("Creating new product: {} with {} images",
                    productRequest.getName(), images != null ? images.length : 0);

            ProductResponseDTO response = productService.createProductWithMultipleImages(productRequest, images);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (JsonProcessingException e) {
            log.error("Error parsing product JSON: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * CREATE PRODUCT with single image (Backward compatibility)
     */
    @PostMapping(value = "/create-single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> createProductWithSingleImage(
            @Valid @RequestPart("product") ProductRequestDTO productRequest,
            @RequestPart("image") MultipartFile image) {

        log.info("Creating new product with single image: {}", productRequest.getName());

        MultipartFile[] images = new MultipartFile[]{image};
        try {
            ProductResponseDTO response = productService.createProductWithMultipleImages(productRequest, images);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Validation error while creating product: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * CREATE PRODUCT without images (For testing or admin use)
     */
    @PostMapping(value = "/create-no-images", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponseDTO> createProductWithoutImages(
            @Valid @RequestBody ProductRequestDTO productRequest) {

        log.info("Creating new product without images: {}", productRequest.getName());

        try {
            ProductResponseDTO response = productService.createProductWithMultipleImages(productRequest, new MultipartFile[0]);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // -------------------- GET PRODUCT ENDPOINTS -------------------- //

    /**
     * GET ALL PRODUCTS
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        log.info("Fetching all products");
        try {
            List<ProductResponseDTO> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * GET PRODUCT BY ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        log.info("Fetching product by ID: {}", id);
        try {
            ProductResponseDTO product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            log.warn("Product not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * GET FEATURED PRODUCTS
     */
//    @GetMapping("/featured")
//    public ResponseEntity<List<ProductResponseDTO>> getFeaturedProducts() {
//        log.info("Fetching featured products");
//        try {
//            List<ProductResponseDTO> featuredProducts = productService.getFeaturedProducts();
//            return ResponseEntity.ok(featuredProducts);
//        } catch (Exception e) {
//            log.error("Error fetching featured products: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//
//    /**
//     * GET BEST SELLER PRODUCTS
//     */
//    @GetMapping("/best-sellers")
//    public ResponseEntity<List<ProductResponseDTO>> getBestSellerProducts() {
//        log.info("Fetching best seller products");
//        try {
//            List<ProductResponseDTO> bestSellers = productService.getBestSellerProducts();
//            return ResponseEntity.ok(bestSellers);
//        } catch (Exception e) {
//            log.error("Error fetching best seller products: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//
//    /**
//     * GET PRODUCTS BY CATEGORY
//     */
//    @GetMapping("/category/{categoryId}")
//    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable Long categoryId) {
//        log.info("Fetching products by category ID: {}", categoryId);
//        try {
//            List<ProductResponseDTO> products = productService.getProductsByCategory(categoryId);
//            return ResponseEntity.ok(products);
//        } catch (Exception e) {
//            log.error("Error fetching products by category: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    // -------------------- UPDATE PRODUCT ENDPOINTS -------------------- //

    /**
     * UPDATE PRODUCT with new images
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> updateProductWithImages(
            @PathVariable Long id,
            @Valid @RequestPart("product") ProductRequestDTO productRequest,
            @RequestPart(value = "newImages", required = false) MultipartFile[] newImages) {

        log.info("Updating product ID: {} with {} new images", id, newImages != null ? newImages.length : 0);

        try {
            ProductResponseDTO updated = productService.updateProductWithImages(id, productRequest, newImages);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Product not found for update with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * UPDATE PRODUCT without images (JSON only)
     */
    @PutMapping(value = "/{id}/details", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponseDTO> updateProductDetails(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO productRequest) {

        log.info("Updating product details for ID: {}", id);

        try {
            ProductResponseDTO updated = productService.updateProductWithImages(id, productRequest, null);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Product not found for update with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating product details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // -------------------- IMAGE MANAGEMENT ENDPOINTS -------------------- //

    /**
     * ADD MORE IMAGES to existing product
     */
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> addMoreImages(
            @PathVariable Long id,
            @RequestPart("additionalImages") MultipartFile[] additionalImages) {

        log.info("Adding {} images to product ID: {}", additionalImages.length, id);

        try {
            ProductResponseDTO updated = productService.addMoreImages(id, additionalImages);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.warn("Product not found for adding images with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error adding images: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * REMOVE IMAGE from product
     */
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Void> removeImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        log.info("Removing image ID: {} from product ID: {}", imageId, productId);

        try {
            productService.removeImage(productId, imageId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Product or image not found for removal: productId={}, imageId={}", productId, imageId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error removing image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * SET PRIMARY IMAGE for product
     */
    @PatchMapping("/{productId}/images/{imageId}/set-primary")
    public ResponseEntity<Void> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        log.info("Setting image ID: {} as primary for product ID: {}", imageId, productId);

        try {
            productService.setPrimaryImage(productId, imageId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.warn("Product or image not found for setting primary: productId={}, imageId={}", productId, imageId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error setting primary image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // -------------------- STOCK MANAGEMENT ENDPOINTS -------------------- //

    /**
     * UPDATE PRODUCT STOCK QUANTITY
     */
//    @PatchMapping("/{id}/stock")
//    public ResponseEntity<Void> updateStockQuantity(
//            @PathVariable Long id,
//            @RequestParam Integer quantity) {
//
//        log.info("Updating stock quantity to {} for product ID: {}", quantity, id);
//
//        try {
//            productService.updateStockQuantity(id, quantity);
//            return ResponseEntity.ok().build();
//        } catch (RuntimeException e) {
//            log.warn("Product not found for stock update with ID: {}", id);
//            return ResponseEntity.notFound().build();
//        } catch (Exception e) {
//            log.error("Error updating stock quantity: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    // -------------------- DELETE PRODUCT ENDPOINTS -------------------- //

    /**
     * DELETE PRODUCT (Soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with ID: {}", id);

        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Product not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * HARD DELETE PRODUCT (Permanent deletion - Admin only)
     */
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteProduct(@PathVariable Long id) {
        log.info("Hard deleting product with ID: {}", id);

        try {
            // You can implement hard delete logic here if needed
            productService.deleteProduct(id); // Currently soft delete
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Product not found for hard deletion with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error hard deleting product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}