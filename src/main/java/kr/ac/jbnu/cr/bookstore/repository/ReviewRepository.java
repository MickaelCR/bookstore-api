package kr.ac.jbnu.cr.bookstore.repository;

import kr.ac.jbnu.cr.bookstore.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"user", "book", "likes"})
    Page<Review> findByBookIdAndDeletedAtIsNull(Long bookId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "book", "likes"})
    Page<Review> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "book", "likes"})
    Optional<Review> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByUserIdAndBookIdAndDeletedAtIsNull(Long userId, Long bookId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId AND r.deletedAt IS NULL")
    Double getAverageRatingByBookId(@Param("bookId") Long bookId);

    long countByBookIdAndDeletedAtIsNull(Long bookId);
}