package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.ac.jbnu.cr.bookstore.dto.request.ReviewRequest;
import kr.ac.jbnu.cr.bookstore.dto.request.ReviewUpdateRequest;
import kr.ac.jbnu.cr.bookstore.dto.response.ErrorResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.MessageResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.PageResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.ReviewResponse;
import kr.ac.jbnu.cr.bookstore.model.Review;
import kr.ac.jbnu.cr.bookstore.security.JwtAuthentication;
import kr.ac.jbnu.cr.bookstore.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
@Tag(name = "Reviews", description = "Review management API")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    private Long getCurrentUserId() {
        JwtAuthentication auth = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return auth.getUserId();
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get reviews for a book")
    @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviewsByBook(
            @PathVariable Long bookId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Review> reviews = reviewService.findByBookId(bookId, pageable);

        List<ReviewResponse> content = reviews.getContent().stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(PageResponse.of(reviews, content));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews by a user")
    @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviewsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Review> reviews = reviewService.findByUserId(userId, pageable);

        List<ReviewResponse> content = reviews.getContent().stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(PageResponse.of(reviews, content));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my reviews")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<ReviewResponse>> getMyReviews(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Review> reviews = reviewService.findByUserId(getCurrentUserId(), pageable);

        List<ReviewResponse> content = reviews.getContent().stream()
                .map(review -> ReviewResponse.from(review, true))
                .collect(Collectors.toList());

        return ResponseEntity.ok(PageResponse.of(reviews, content));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        Review review = reviewService.findById(id);
        return ResponseEntity.ok(ReviewResponse.from(review));
    }

    @PostMapping
    @Operation(summary = "Create a new review")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Already reviewed this book",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        Review review = reviewService.create(getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewResponse.from(review));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a review")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Cannot update another user's review",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateRequest request) {
        Review review = reviewService.update(getCurrentUserId(), id, request);
        return ResponseEntity.ok(ReviewResponse.from(review));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Cannot delete another user's review",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MessageResponse> deleteReview(@PathVariable Long id) {
        reviewService.delete(getCurrentUserId(), id);
        return ResponseEntity.ok(MessageResponse.of("Review deleted successfully"));
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "Like a review")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review liked successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Already liked this review",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MessageResponse> likeReview(@PathVariable Long id) {
        reviewService.likeReview(getCurrentUserId(), id);
        return ResponseEntity.ok(MessageResponse.of("Review liked successfully"));
    }

    @DeleteMapping("/{id}/like")
    @Operation(summary = "Unlike a review")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review unliked successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Like not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MessageResponse> unlikeReview(@PathVariable Long id) {
        reviewService.unlikeReview(getCurrentUserId(), id);
        return ResponseEntity.ok(MessageResponse.of("Review unliked successfully"));
    }
}