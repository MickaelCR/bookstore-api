package kr.ac.jbnu.cr.bookstore.service;

import kr.ac.jbnu.cr.bookstore.dto.request.ReviewRequest;
import kr.ac.jbnu.cr.bookstore.dto.request.ReviewUpdateRequest;
import kr.ac.jbnu.cr.bookstore.exception.DuplicateResourceException;
import kr.ac.jbnu.cr.bookstore.exception.ForbiddenException;
import kr.ac.jbnu.cr.bookstore.exception.ResourceNotFoundException;
import kr.ac.jbnu.cr.bookstore.model.Book;
import kr.ac.jbnu.cr.bookstore.model.Review;
import kr.ac.jbnu.cr.bookstore.model.ReviewLike;
import kr.ac.jbnu.cr.bookstore.model.User;
import kr.ac.jbnu.cr.bookstore.repository.BookRepository;
import kr.ac.jbnu.cr.bookstore.repository.ReviewLikeRepository;
import kr.ac.jbnu.cr.bookstore.repository.ReviewRepository;
import kr.ac.jbnu.cr.bookstore.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ReviewLikeRepository reviewLikeRepository,
                         BookRepository bookRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Find review by ID
     */
    public Review findById(Long id) {
        return reviewRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));
    }

    /**
     * Find reviews by book
     */
    public Page<Review> findByBookId(Long bookId, Pageable pageable) {
        return reviewRepository.findByBookIdAndDeletedAtIsNull(bookId, pageable);
    }

    /**
     * Find reviews by user
     */
    public Page<Review> findByUserId(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdAndDeletedAtIsNull(userId, pageable);
    }

    /**
     * Create a new review
     */
    @Transactional
    public Review create(Long userId, ReviewRequest request) {
        // Check if user already reviewed this book
        if (reviewRepository.existsByUserIdAndBookIdAndDeletedAtIsNull(userId, request.getBookId())) {
            throw new DuplicateResourceException("You have already reviewed this book");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Book book = bookRepository.findByIdAndIsActiveTrue(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", request.getBookId()));

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return reviewRepository.save(review);
    }

    /**
     * Update a review
     */
    @Transactional
    public Review update(Long userId, Long reviewId, ReviewUpdateRequest request) {
        Review review = findById(reviewId);

        // Check ownership
        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only update your own reviews");
        }

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        return reviewRepository.save(review);
    }

    /**
     * Delete a review (soft delete)
     */
    @Transactional
    public void delete(Long userId, Long reviewId) {
        Review review = findById(reviewId);

        // Check ownership
        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own reviews");
        }

        review.setDeletedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    /**
     * Like a review
     */
    @Transactional
    public void likeReview(Long userId, Long reviewId) {
        // Check if already liked
        if (reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new DuplicateResourceException("You have already liked this review");
        }

        Review review = findById(reviewId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        ReviewLike like = ReviewLike.builder()
                .review(review)
                .user(user)
                .build();

        reviewLikeRepository.save(like);
    }

    /**
     * Unlike a review
     */
    @Transactional
    public void unlikeReview(Long userId, Long reviewId) {
        ReviewLike like = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found"));

        reviewLikeRepository.delete(like);
    }

    /**
     * Check if user liked a review
     */
    public boolean isLikedByUser(Long reviewId, Long userId) {
        return reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);
    }

    /**
     * Get like count for a review
     */
    public long getLikeCount(Long reviewId) {
        return reviewLikeRepository.countByReviewId(reviewId);
    }
}