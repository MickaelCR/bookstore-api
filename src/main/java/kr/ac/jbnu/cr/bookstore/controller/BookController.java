package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.ac.jbnu.cr.bookstore.dto.request.BookRequest;
import kr.ac.jbnu.cr.bookstore.dto.response.BookResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.ErrorResponse;
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
    @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
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
    @ApiResponse(responseCode = "200", description = "Top books retrieved successfully")
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        Book book = bookService.findByIdAndIncrementView(id);
        return ResponseEntity.ok(BookResponse.from(book,
                bookService.getAverageRating(id),
                bookService.getReviewCount(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new book (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "ISBN already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        Book book = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BookResponse.from(book));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "ISBN already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        Book book = bookService.update(id, request);
        return ResponseEntity.ok(BookResponse.from(book));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MessageResponse> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.ok(MessageResponse.of("Book deleted successfully"));
    }
}