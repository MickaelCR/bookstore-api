package kr.ac.jbnu.cr.bookstore.service;

import kr.ac.jbnu.cr.bookstore.exception.DuplicateResourceException;
import kr.ac.jbnu.cr.bookstore.exception.ResourceNotFoundException;
import kr.ac.jbnu.cr.bookstore.model.Book;
import kr.ac.jbnu.cr.bookstore.model.Favorite;
import kr.ac.jbnu.cr.bookstore.model.User;
import kr.ac.jbnu.cr.bookstore.repository.BookRepository;
import kr.ac.jbnu.cr.bookstore.repository.FavoriteRepository;
import kr.ac.jbnu.cr.bookstore.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           BookRepository bookRepository,
                           UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get user's favorites (paginated)
     */
    public Page<Favorite> findByUserId(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserId(userId, pageable);
    }

    /**
     * Add book to favorites
     */
    @Transactional
    public Favorite addFavorite(Long userId, Long bookId) {
        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new DuplicateResourceException("Book is already in favorites");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Book book = bookRepository.findByIdAndIsActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", bookId));

        Favorite favorite = Favorite.builder()
                .user(user)
                .book(book)
                .build();

        return favoriteRepository.save(favorite);
    }

    /**
     * Remove book from favorites
     */
    @Transactional
    public void removeFavorite(Long userId, Long bookId) {
        Favorite favorite = favoriteRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

        favoriteRepository.delete(favorite);
    }

    /**
     * Check if book is in user's favorites
     */
    public boolean isFavorite(Long userId, Long bookId) {
        return favoriteRepository.existsByUserIdAndBookId(userId, bookId);
    }

    /**
     * Get favorite count for a book
     */
    public long getFavoriteCount(Long bookId) {
        return favoriteRepository.countByBookId(bookId);
    }
}