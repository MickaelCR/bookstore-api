package kr.ac.jbnu.cr.bookstore.repository;

import kr.ac.jbnu.cr.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"categories"})
    Page<Book> findByIsActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"categories"})
    Optional<Book> findByIdAndIsActiveTrue(Long id);

    @EntityGraph(attributePaths = {"categories"})
    @Query("SELECT b FROM Book b WHERE b.isActive = true AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"categories"})
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE b.isActive = true AND c.id = :categoryId")
    Page<Book> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"categories"})
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN b.categories c WHERE b.isActive = true " +
            "AND (:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:minPrice IS NULL OR b.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR b.price <= :maxPrice)")
    Page<Book> searchBooks(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    boolean existsByIsbn(String isbn);

    @EntityGraph(attributePaths = {"categories"})
    List<Book> findTop10ByIsActiveTrueOrderByViewCountDesc();
}