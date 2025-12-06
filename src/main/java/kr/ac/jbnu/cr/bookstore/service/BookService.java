package kr.ac.jbnu.cr.bookstore.service;

import kr.ac.jbnu.cr.bookstore.dto.request.BookRequest;
import kr.ac.jbnu.cr.bookstore.exception.DuplicateResourceException;
import kr.ac.jbnu.cr.bookstore.exception.ResourceNotFoundException;
import kr.ac.jbnu.cr.bookstore.model.Book;
import kr.ac.jbnu.cr.bookstore.model.Category;
import kr.ac.jbnu.cr.bookstore.repository.BookRepository;
import kr.ac.jbnu.cr.bookstore.repository.CategoryRepository;
import kr.ac.jbnu.cr.bookstore.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    public BookService(BookRepository bookRepository,
                       CategoryRepository categoryRepository,
                       ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Find all active books (paginated)
     */
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findByIsActiveTrue(pageable);
    }

    /**
     * Find book by ID
     */
    public Book findById(Long id) {
        return bookRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
    }

    /**
     * Find book by ID and increment view count
     */
    @Transactional
    public Book findByIdAndIncrementView(Long id) {
        Book book = findById(id);
        book.setViewCount(book.getViewCount() + 1);
        return bookRepository.save(book);
    }

    /**
     * Search books by keyword
     */
    public Page<Book> searchByKeyword(String keyword, Pageable pageable) {
        return bookRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * Find books by category
     */
    public Page<Book> findByCategory(Long categoryId, Pageable pageable) {
        return bookRepository.findByCategoryId(categoryId, pageable);
    }

    /**
     * Search books with filters
     */
    public Page<Book> searchBooks(String keyword, Long categoryId,
                                  BigDecimal minPrice, BigDecimal maxPrice,
                                  Pageable pageable) {
        return bookRepository.searchBooks(keyword, categoryId, minPrice, maxPrice, pageable);
    }

    /**
     * Create a new book
     */
    @Transactional
    public Book create(BookRequest request) {
        // Check duplicate ISBN
        if (request.getIsbn() != null && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("Book with ISBN already exists: " + request.getIsbn());
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .summary(request.getSummary())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .publicationDate(request.getPublicationDate())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .categories(new ArrayList<>())
                .build();

        // Add categories
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            book.setCategories(categories);
        }

        return bookRepository.save(book);
    }

    /**
     * Update a book
     */
    @Transactional
    public Book update(Long id, BookRequest request) {
        Book book = findById(id);

        // Check duplicate ISBN (if changed)
        if (request.getIsbn() != null
                && !request.getIsbn().equals(book.getIsbn())
                && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("Book with ISBN already exists: " + request.getIsbn());
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setSummary(request.getSummary());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setPublicationDate(request.getPublicationDate());

        if (request.getStockQuantity() != null) {
            book.setStockQuantity(request.getStockQuantity());
        }

        // Update categories
        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            book.setCategories(categories);
        }

        return bookRepository.save(book);
    }

    /**
     * Delete a book (soft delete)
     */
    @Transactional
    public void delete(Long id) {
        Book book = findById(id);
        book.setIsActive(false);
        bookRepository.save(book);
    }

    /**
     * Get average rating for a book
     */
    public Double getAverageRating(Long bookId) {
        return reviewRepository.getAverageRatingByBookId(bookId);
    }

    /**
     * Get review count for a book
     */
    public Long getReviewCount(Long bookId) {
        return reviewRepository.countByBookIdAndDeletedAtIsNull(bookId);
    }

    /**
     * Get top books by view count
     */
    public List<Book> getTopBooks() {
        return bookRepository.findTop10ByIsActiveTrueOrderByViewCountDesc();
    }
}