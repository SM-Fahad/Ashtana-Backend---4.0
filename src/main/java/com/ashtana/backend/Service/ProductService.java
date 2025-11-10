package com.ashtana.backend.Service;

import com.ashtana.backend.DTO.RequestDTO.ProductRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.ProductResponseDTO;
import com.ashtana.backend.Entity.*;
import com.ashtana.backend.Enums.ProductStatus;
import com.ashtana.backend.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepo categoryRepository;
    private final SubCategoryRepo subCategoryRepository;
    private final ColorRepo colorRepository;
    private final SizeRepo sizeRepository;
    private final UserRepo userRepository;
    private final FileDataRepository fileDataRepository;

    // -------------------- FILE STORAGE CONFIGURATION -------------------- //
    private final String uploadDir = "uploads/products/";

    /**
     * Save multiple image files and create FileData entities
     */
    private List<FileData> saveMultipleImageFiles(MultipartFile[] files, Product product) {
        List<FileData> savedImages = new ArrayList<>();

        if (files == null || files.length == 0) {
            return savedImages;
        }

        try {
            Files.createDirectories(Paths.get(uploadDir));

            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                if (file != null && !file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String fileExtension = getFileExtension(originalFileName);
                    String fileName = System.currentTimeMillis() + "_" + i + "_" + originalFileName;
                    Path filePath = Paths.get(uploadDir + fileName);

                    // Save file to disk
                    Files.write(filePath, file.getBytes());

                    // Create FileData entity
                    FileData fileData = FileData.builder()
                            .fileName(originalFileName)
                            .filePath("/uploads/products/" + fileName)
                            .fileType(fileExtension)
                            .fileSize(file.getSize())
                            .mimeType(file.getContentType())
                            .isPrimary(i == 0) // First image is primary
                            .sortOrder(i)
                            .product(product)
                            .isActive(true)
                            .altText(product.getName() + " - Image " + (i + 1)) // Generate alt text
                            .build();

                    FileData savedFileData = fileDataRepository.save(fileData);
                    savedImages.add(savedFileData);

                    log.info("Image saved successfully: {}", originalFileName);
                }
            }
        } catch (IOException e) {
            log.error("Failed to save images: {}", e.getMessage());
            throw new RuntimeException("Failed to save images: " + e.getMessage(), e);
        }

        return savedImages;
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    // -------------------- CREATE PRODUCT WITH MULTIPLE IMAGES -------------------- //
    @Transactional
    public ProductResponseDTO createProductWithMultipleImages(ProductRequestDTO dto, MultipartFile[] imageFiles) {
        log.info("Creating new product with {} images: {}",
                imageFiles != null ? imageFiles.length : 0, dto.getName());

        // Validate that at least one image is provided
        if (imageFiles == null || imageFiles.length == 0) {
            throw new IllegalArgumentException("At least one product image is required");
        }

        // Validate image files
        for (MultipartFile file : imageFiles) {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("One or more image files are empty");
            }
            if (!isValidImageType(file.getContentType())) {
                throw new IllegalArgumentException("Invalid image type: " + file.getContentType());
            }
        }

        // Build product from DTO
        Product product = buildProductFromDto(dto);

        // Save product first to get ID
        Product savedProduct = productRepository.save(product);
        log.info("Product saved with ID: {}", savedProduct.getId());

        // Handle multiple image uploads
        List<FileData> savedImages = saveMultipleImageFiles(imageFiles, savedProduct);
        savedProduct.setImages(savedImages);

        // Update Many-to-Many relationships
        updateProductSizes(savedProduct, dto.getSizeIds());
        updateProductColors(savedProduct, dto.getColorIds());

        // Final save with all relationships
        Product finalProduct = productRepository.save(savedProduct);

        log.info("Product created successfully with ID: {} and {} images",
                finalProduct.getId(), finalProduct.getImages().size());

        return toDto(finalProduct);
    }

    /**
     * Validate image file type
     */
    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/gif") ||
                        contentType.equals("image/webp")
        );
    }

    // -------------------- BUILD PRODUCT FROM DTO -------------------- //
    private Product buildProductFromDto(ProductRequestDTO dto) {
        // Validate required entities
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoryId()));

        SubCategory subCategory = null;
        if (dto.getSubCategoryId() != null) {
            subCategory = subCategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("SubCategory not found with id: " + dto.getSubCategoryId()));
        }

        User user = null;
        if (dto.getUserName() != null) {
            user = userRepository.findById(dto.getUserName())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserName()));
        }

        // Generate SKU if not provided
        String sku = dto.getSku();
        if (sku == null || sku.trim().isEmpty()) {
            sku = generateSku(dto.getName());
        }

        // Build product
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStock())
                .sku(sku)
                .category(category)
                .subCategory(subCategory)
                .user(user)
                .status(dto.getStatus() != null ? dto.getStatus() : ProductStatus.ACTIVE)
                .weight(dto.getWeight())
                .dimensions(dto.getDimensions())
                .material(dto.getMaterial())
                .brand(dto.getBrand())
                .isFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false)
                .isBestSeller(dto.getIsBestSeller() != null ? dto.getIsBestSeller() : false)
                .availableSizes(new HashSet<>())
                .availableColors(new HashSet<>())
                .images(new ArrayList<>())
                .reviews(new ArrayList<>())
                .build();
    }

    // -------------------- UPDATE PRODUCT SIZES (Many-to-Many) -------------------- //
    @Transactional
    public void updateProductSizes(Product product, Set<Long> sizeIds) {
        if (sizeIds == null) return;

        // Clear existing sizes
        product.getAvailableSizes().clear();

        // Add new sizes
        if (!sizeIds.isEmpty()) {
            Set<Size> sizes = new HashSet<>(sizeRepository.findAllById(sizeIds));
            // Validate that all sizes were found
            if (sizes.size() != sizeIds.size()) {
                throw new RuntimeException("One or more sizes not found");
            }
            product.setAvailableSizes(sizes);
        }
    }

    // -------------------- UPDATE PRODUCT COLORS (Many-to-Many) -------------------- //
    @Transactional
    public void updateProductColors(Product product, Set<Long> colorIds) {
        if (colorIds == null) return;

        // Clear existing colors
        product.getAvailableColors().clear();

        // Add new colors
        if (!colorIds.isEmpty()) {
            Set<Color> colors = new HashSet<>(colorRepository.findAllById(colorIds));
            // Validate that all colors were found
            if (colors.size() != colorIds.size()) {
                throw new RuntimeException("One or more colors not found");
            }
            product.setAvailableColors(colors);
        }
    }

    // -------------------- GET PRODUCT METHODS -------------------- //
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return toDto(product);
    }

    public List<ProductResponseDTO> getAllProducts() {

        List<Product> pd = productRepository.findAll();


        return pd.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // -------------------- UPDATE PRODUCT WITH MULTIPLE IMAGES -------------------- //
    @Transactional
    public ProductResponseDTO updateProductWithImages(Long id, ProductRequestDTO dto, MultipartFile[] newImageFiles) {
        log.info("Updating product with ID: {}", id);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        // Update basic fields
        updateProductFields(existingProduct, dto);

        // Update Many-to-Many relationships
        updateProductSizes(existingProduct, dto.getSizeIds());
        updateProductColors(existingProduct, dto.getColorIds());

        // Handle new images if provided
        if (newImageFiles != null && newImageFiles.length > 0) {
            // Validate new images
            for (MultipartFile file : newImageFiles) {
                if (file != null && !file.isEmpty() && !isValidImageType(file.getContentType())) {
                    throw new IllegalArgumentException("Invalid image type: " + file.getContentType());
                }
            }

            List<FileData> newImages = saveMultipleImageFiles(newImageFiles, existingProduct);
            existingProduct.getImages().addAll(newImages);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully with ID: {} and total {} images",
                updatedProduct.getId(), updatedProduct.getImages().size());

        return toDto(updatedProduct);
    }

    private void updateProductFields(Product product, ProductRequestDTO dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStock());
        product.setStatus(dto.getStatus());
        product.setWeight(dto.getWeight());
        product.setDimensions(dto.getDimensions());
        product.setMaterial(dto.getMaterial());
        product.setBrand(dto.getBrand());
        product.setIsFeatured(dto.getIsFeatured());
        product.setIsBestSeller(dto.getIsBestSeller());

        // Update category and subcategory if provided
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        if (dto.getSubCategoryId() != null) {
            SubCategory subCategory = subCategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("SubCategory not found"));
            product.setSubCategory(subCategory);
        }
    }

    // -------------------- IMAGE MANAGEMENT METHODS -------------------- //
    @Transactional
    public ProductResponseDTO addMoreImages(Long productId, MultipartFile[] additionalImageFiles) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        if (additionalImageFiles == null || additionalImageFiles.length == 0) {
            throw new IllegalArgumentException("No image files provided");
        }

        // Validate images
        for (MultipartFile file : additionalImageFiles) {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("One or more image files are empty");
            }
            if (!isValidImageType(file.getContentType())) {
                throw new IllegalArgumentException("Invalid image type: " + file.getContentType());
            }
        }

        List<FileData> newImages = saveMultipleImageFiles(additionalImageFiles, product);
        product.getImages().addAll(newImages);

        Product updatedProduct = productRepository.save(product);
        log.info("Added {} new images to product ID: {}", newImages.size(), productId);

        return toDto(updatedProduct);
    }

    @Transactional
    public void removeImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        FileData imageToRemove = fileDataRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));

        if (!imageToRemove.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Image does not belong to the specified product");
        }

        // Remove file from filesystem
        try {
            String filePath = imageToRemove.getFilePath().replace("/uploads/products/", uploadDir);
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            log.warn("Failed to delete image file from filesystem: {}", e.getMessage());
        }

        product.getImages().remove(imageToRemove);
        fileDataRepository.delete(imageToRemove);

        // If we removed the primary image, set a new primary
        if (imageToRemove.getIsPrimary() && !product.getImages().isEmpty()) {
            product.getImages().get(0).setIsPrimary(true);
        }

        productRepository.save(product);
        log.info("Image removed successfully: {}", imageId);
    }

    @Transactional
    public void setPrimaryImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        FileData newPrimaryImage = fileDataRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));

        if (!newPrimaryImage.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Image does not belong to the specified product");
        }

        // Reset all images to non-primary
        product.getImages().forEach(image -> image.setIsPrimary(false));

        // Set the new primary image
        newPrimaryImage.setIsPrimary(true);

        productRepository.save(product);
        log.info("Set primary image to: {}", imageId);
    }

    // -------------------- MAPPING TO DTO -------------------- //
//    public ProductResponseDTO toDto(Product product) {
//        ProductResponseDTO dto = new ProductResponseDTO();
//        dto.setId(product.getId());
//        dto.setName(product.getName());
//        dto.setDescription(product.getDescription());
//        dto.setPrice(product.getPrice());
//        dto.setStock(product.getStockQuantity());
//        dto.setSku(product.getSku());
//        dto.setStatus(product.getStatus());
//
//        // Category information
//        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
//        dto.setSubCategoryName(product.getSubCategory() != null ? product.getSubCategory().getName() : null);
//
//        // Many-to-Many relationships
//        dto.setAvailableSizes(product.getAvailableSizes().stream()
//                .map(Size::getSizeName)
//                .collect(Collectors.toSet()));
//        dto.setAvailableColors(product.getAvailableColors().stream()
//                .map(color -> color.getColorName() + " (" + color.getColorCode() + ")")
//                .collect(Collectors.toSet()));
//
//        // FileData images with full details
//        dto.setImageUrls(product.getImages().stream()
//                .filter(FileData::getIsActive)
//                .sorted((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()))
//                .map(FileData::getFilePath)
//                .collect(Collectors.toList()));
//
//        // Image details for frontend
//        dto.setImageDetails(product.getImages().stream()
//                .filter(FileData::getIsActive)
//                .sorted((a, b) -> a.getSortOrder().compareTo(b.getSortOrder()))
//                .map(this::mapFileDataToImageDetail)
//                .collect(Collectors.toList()));
//
//        // Additional fields
//        dto.setWeight(product.getWeight());
//        dto.setDimensions(product.getDimensions());
//        dto.setMaterial(product.getMaterial());
//        dto.setBrand(product.getBrand());
//        dto.setIsFeatured(product.getIsFeatured());
//        dto.setIsBestSeller(product.getIsBestSeller());
//        dto.setCreatedAt(product.getCreatedAt());
//        dto.setUpdatedAt(product.getUpdatedAt());
//
//        return dto;
//    }
//    public ProductResponseDTO toDto(Product product) {
//        ProductResponseDTO dto = new ProductResponseDTO();
//
//        dto.setId(product.getId());
//        dto.setName(product.getName());
//        dto.setDescription(product.getDescription());
//        dto.setPrice(product.getPrice());
//        dto.setStock(product.getStockQuantity());
//        dto.setSku(product.getSku());
//        dto.setStatus(product.getStatus());
//
//        // Category info
//        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
//        dto.setSubCategoryName(product.getSubCategory() != null ? product.getSubCategory().getName() : null);
//

    /// /        // Sizes and colors
//        dto.setAvailableSizes(Optional.ofNullable(product.getAvailableSizes())
//                .orElse(Collections.emptySet())
//                .stream()
//                .map(Size::getSizeName)
//                .collect(Collectors.toSet()));
//
//        dto.setAvailableColors(Optional.ofNullable(product.getAvailableColors())
//                .orElse(Collections.emptySet())
//                .stream()
//                .map(color -> color.getColorName() + " (" + color.getColorCode() + ")")
//                .collect(Collectors.toSet()));
//
//        // Images (optimized)
//        List<FileData> activeSortedImages = Optional.ofNullable(product.getImages())
//                .orElse(Collections.emptyList())
//                .stream()
//                .filter(FileData::getIsActive)
//                .sorted(Comparator.comparing(FileData::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
//                .toList();
//
//        dto.setImageUrls(activeSortedImages.stream().map(FileData::getFilePath).toList());
//        dto.setImageDetails(activeSortedImages.stream().map(this::mapFileDataToImageDetail).toList());
//
//        // Primary image
//        FileData primaryImage = product.getPrimaryImage();
//        dto.setImageUrls(primaryImage != null ? Collections.singletonList(primaryImage.getFilePath()) : null);
//
//        // Other fields
//        dto.setWeight(product.getWeight());
//        dto.setDimensions(product.getDimensions());
//        dto.setMaterial(product.getMaterial());
//        dto.setBrand(product.getBrand());
//        dto.setIsFeatured(product.getIsFeatured());
//        dto.setIsBestSeller(product.getIsBestSeller());
//        dto.setCreatedAt(product.getCreatedAt());
//        dto.setUpdatedAt(product.getUpdatedAt());
//
//        return dto;
//    }
//
    public ProductResponseDTO toDto(Product product) {
        try {


        ProductResponseDTO dto = new ProductResponseDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStockQuantity());
        dto.setSku(product.getSku());
        dto.setStatus(product.getStatus());

        // ✅ Category and Subcategory
        dto.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setSubCategoryName(product.getSubCategory() != null ? product.getSubCategory().getName() : null);

        // ✅ Fetch Colors and Sizes from DB (fresh data)
        Set<Color> colors = new HashSet<>();
        Set<Size> sizes = new HashSet<>();

        if (product.getId() != null) {
            colors = new HashSet<>(colorRepository.findByProducts_Id(product.getId()));
            sizes = new HashSet<>(sizeRepository.findByProducts_Id(product.getId()));
        }

        dto.setAvailableColors(colors.stream()
                .map(color -> color.getColorName() + " (" + color.getColorCode() + ")")
                .collect(Collectors.toSet()));

        dto.setAvailableSizes(sizes.stream()
                .map(Size::getSizeName)
                .collect(Collectors.toSet()));

        // ✅ Handle Images
        List<FileData> activeSortedImages = Optional.ofNullable(product.getImages())
                .orElse(Collections.emptyList())
                .stream()
                .filter(FileData::getIsActive)
                .sorted(Comparator.comparing(FileData::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .toList();

        dto.setImageUrls(activeSortedImages.stream().map(FileData::getFilePath).toList());
        dto.setImageDetails(activeSortedImages.stream().map(this::mapFileDataToImageDetail).toList());

        // ✅ Primary Image (if exists)
        FileData primaryImage = product.getPrimaryImage();
        if (primaryImage != null) {
            dto.setImageUrls(Arrays.asList(primaryImage.getFilePath()));
        } else if (!activeSortedImages.isEmpty()) {
            dto.setImageUrls(Arrays.asList(activeSortedImages.get(0).getFilePath()));
        } else {
            dto.setImageUrls(Arrays.asList("assets/images/placeholder-product.jpg"));
        }

        // ✅ Other product fields
        dto.setWeight(product.getWeight());
        dto.setDimensions(product.getDimensions());
        dto.setMaterial(product.getMaterial());
        dto.setBrand(product.getBrand());
        dto.setIsFeatured(product.getIsFeatured());
        dto.setIsBestSeller(product.getIsBestSeller());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        return dto;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    private ImageDetailDTO mapFileDataToImageDetail(FileData fileData) {
//        ImageDetailDTO detail = new ImageDetailDTO();
//        detail.setFileName(fileData.getFileName());
//        detail.setFilePath(fileData.getFilePath());
//        detail.setSortOrder(fileData.getSortOrder());
//        return detail;
//    }

    private ProductResponseDTO.ImageDetail mapFileDataToImageDetail(FileData fileData) {
        ProductResponseDTO.ImageDetail detail = new ProductResponseDTO.ImageDetail();
        detail.setId(fileData.getId());
        detail.setUrl(fileData.getFilePath());
        detail.setAltText(fileData.getAltText());
        detail.setIsPrimary(fileData.getIsPrimary());
        detail.setSortOrder(fileData.getSortOrder());
        return detail;
    }

    // -------------------- UTILITY METHODS -------------------- //
    private String generateSku(String productName) {
        String base = productName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return base.substring(0, Math.min(base.length(), 8)) + "_" + timestamp.substring(timestamp.length() - 6);
    }

    // -------------------- DELETE PRODUCT -------------------- //
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        // Delete image files from filesystem
        product.getImages().forEach(image -> {
            try {
                String filePath = image.getFilePath().replace("/uploads/products/", uploadDir);
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException e) {
                log.warn("Failed to delete image file: {}", e.getMessage());
            }
        });

        // Soft delete by setting status to INACTIVE
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
        log.info("Product soft deleted with ID: {}", id);
    }
}