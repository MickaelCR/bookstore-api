package kr.ac.jbnu.cr.bookstore.service;

import kr.ac.jbnu.cr.bookstore.dto.request.BookRequest;
import kr.ac.jbnu.cr.bookstore.exception.DuplicateResourceException;
import kr.ac.jbnu.cr.bookstore.exception.ResourceNotFoundException;
import kr.ac.jbnu.cr.bookstore.model.Book;
import kr.ac.jbnu.cr.bookstore.repository.BookRepository;
import kr.ac.jbnu.cr.bookstore.repository.CategoryRepository;
import kr.ac.jbnu.cr.bookstore.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .price(BigDecimal.valueOf(29.99))
                .stockQuantity(100)
                .viewCount(0L)
                .isActive(true)
                .categories(new ArrayList<>())
                .build();

        bookRequest = BookRequest.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .price(BigDecimal.valueOf(29.99))
                .stockQuantity(100)
                .build();
    }

    @Test
    @DisplayName("Find by ID - Success")
    void findById_Success() {
        when(bookRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testBook));

        Book result = bookService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Book");
    }

    @Test
    @DisplayName("Find by ID - Not found throws exception")
    void findById_NotFound_ThrowsException() {
        when(bookRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Find by ID and increment view - Success")
    void findByIdAndIncrementView_Success() {
        when(bookRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.findByIdAndIncrementView(1L);

        assertThat(result.getViewCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Create - Success")
    void create_Success() {
        when(bookRepository.existsByIsbn(any())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.create(bookRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Book");
    }

    @Test
    @DisplayName("Create - Duplicate ISBN throws exception")
    void create_DuplicateIsbn_ThrowsException() {
        when(bookRepository.existsByIsbn(any())).thenReturn(true);

        assertThatThrownBy(() -> bookService.create(bookRequest))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("Delete - Soft delete sets isActive to false")
    void delete_SoftDelete() {
        when(bookRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        bookService.delete(1L);

        assertThat(testBook.getIsActive()).isFalse();
    }
}