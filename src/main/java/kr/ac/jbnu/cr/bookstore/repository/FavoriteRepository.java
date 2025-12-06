package kr.ac.jbnu.cr.bookstore.repository;

import kr.ac.jbnu.cr.bookstore.model.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndBookId(Long userId, Long bookId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    void deleteByUserIdAndBookId(Long userId, Long bookId);

    long countByBookId(Long bookId);
}