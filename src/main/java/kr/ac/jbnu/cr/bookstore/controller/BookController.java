package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.ac.jbnu.cr.bookstore.dto.request.BookRequest;
import kr.ac.jbnu.cr.bookstore.dto.response.BookResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.MessageResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.PageResponse;
import kr.ac.jbnu.cr.bookstore.model.Book;
import kr.ac.jbnu.cr.bookstore.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Book management API")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @Operation(summary = "Get all books with search and filters")
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        Page<Book> books = bookService.searchBooks(keyword, categoryId, minPrice, maxPrice, pageable);

        List<BookResponse> content = books.getContent().stream()
                .map(book -> BookResponse.from(book,
                        bookService.getAverageRating(book.getId()),
                        bookService.getReviewCount(book.getId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(PageResponse.of(books, content));
    }

    @GetMapping("/top")
    @Operation(summary = "Get top 10 books by view count")
    public ResponseEntity<List<BookResponse>> getTopBooks() {
        List<BookResponse> books = bookService.getTopBooks().stream()
                .map(book -> BookResponse.from(book,
                        bookService.getAverageRating(book.getId()),
                        bookService.getReviewCount(book.getId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        Book book = bookService.findByIdAndIncrementView(id);
        return ResponseEntity.ok(BookResponse.from(book,
                bookService.getAverageRating(id),
                bookService.getReviewCount(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new book (admin only)")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        Book book = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BookResponse.from(book));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book (admin only)")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        Book book = bookService.update(id, request);
        return ResponseEntity.ok(BookResponse.from(book));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book (admin only)")
    public ResponseEntity<MessageResponse> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.ok(MessageResponse.of("Book deleted successfully"));
    }
}
