package kr.ac.jbnu.cr.bookstore.dto.response;

import kr.ac.jbnu.cr.bookstore.model.Book;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String summary;
    private String isbn;
    private BigDecimal price;
    private LocalDate publicationDate;
    private Integer stockQuantity;
    private Long viewCount;
    private List<CategoryResponse> categories;
    private Double averageRating;
    private Long reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .summary(book.getSummary())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .publicationDate(book.getPublicationDate())
                .stockQuantity(book.getStockQuantity())
                .viewCount(book.getViewCount())
                .categories(book.getCategories().stream()
                        .map(CategoryResponse::from)
                        .collect(Collectors.toList()))
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    public static BookResponse from(Book book, Double averageRating, Long reviewCount) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .summary(book.getSummary())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .publicationDate(book.getPublicationDate())
                .stockQuantity(book.getStockQuantity())
                .viewCount(book.getViewCount())
                .categories(book.getCategories().stream()
                        .map(CategoryResponse::from)
                        .collect(Collectors.toList()))
                .averageRating(averageRating)
                .reviewCount(reviewCount)
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}