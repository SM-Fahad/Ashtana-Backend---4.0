package com.ashtana.backend.Service;


import com.ashtana.backend.DTO.RequestDTO.ReviewRequestDTO;
import com.ashtana.backend.DTO.ResponseDTO.ReviewResponseDTO;
import com.ashtana.backend.Entity.Product;
import com.ashtana.backend.Entity.Review;
import com.ashtana.backend.Entity.User;
import com.ashtana.backend.Repository.ProductRepo;
import com.ashtana.backend.Repository.ReviewRepo;
import com.ashtana.backend.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepo reviewRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;

    // 🔹 Create Review
    public ReviewResponseDTO createReview(ReviewRequestDTO dto) {
        User user = userRepo.findById(dto.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());

        Review saved = reviewRepo.save(review);
        return convertToResponse(saved);
    }

    // 🔹 Get All Reviews
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepo.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Get Reviews by Product
    public List<ReviewResponseDTO> getReviewsByProduct(Long productId) {
        return reviewRepo.findByProductId(productId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Get Reviews by User
    public List<ReviewResponseDTO> getReviewsByUser(String userId) {
        return reviewRepo.findByUser_UserName(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Delete Review
    public void deleteReview(Long id) {
        reviewRepo.deleteById(id);
    }

    // 🔹 Convert Entity to ResponseDTO
    private ReviewResponseDTO convertToResponse(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setUserName(review.getUser().getUserName());
        dto.setProductName(review.getProduct().getName());
        return dto;
    }
}
