package kr.ac.jbnu.cr.bookstore.repository;

import kr.ac.jbnu.cr.bookstore.model.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"items", "items.book"})
    Optional<Cart> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}