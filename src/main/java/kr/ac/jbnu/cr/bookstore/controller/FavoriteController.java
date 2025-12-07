package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.jbnu.cr.bookstore.dto.response.BookResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.MessageResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.PageResponse;
import kr.ac.jbnu.cr.bookstore.model.Favorite;
import kr.ac.jbnu.cr.bookstore.security.JwtAuthentication;
import kr.ac.jbnu.cr.bookstore.service.BookService;
import kr.ac.jbnu.cr.bookstore.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorites")
@Tag(name = "Favorites", description = "Favorites management API")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final BookService bookService;

    public FavoriteController(FavoriteService favoriteService, BookService bookService) {
        this.favoriteService = favoriteService;
        this.bookService = bookService;
    }

    private Long getCurrentUserId() {
        JwtAuthentication auth = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return auth.getUserId();
    }

    @GetMapping
    @Operation(summary = "Get my favorite books")
    public ResponseEntity<PageResponse<BookResponse>> getMyFavorites(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Favorite> favorites = favoriteService.findByUserId(getCurrentUserId(), pageable);

        List<BookResponse> content = favorites.getContent().stream()
                .map(fav -> BookResponse.from(fav.getBook(),
                        bookService.getAverageRating(fav.getBook().getId()),
                        bookService.getReviewCount(fav.getBook().getId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(PageResponse.of(favorites, content));
    }

    @PostMapping("/{bookId}")
    @Operation(summary = "Add book to favorites")
    public ResponseEntity<MessageResponse> addFavorite(@PathVariable Long bookId) {
        favoriteService.addFavorite(getCurrentUserId(), bookId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MessageResponse.of("Book added to favorites"));
    }

    @DeleteMapping("/{bookId}")
    @Operation(summary = "Remove book from favorites")
    public ResponseEntity<MessageResponse> removeFavorite(@PathVariable Long bookId) {
        favoriteService.removeFavorite(getCurrentUserId(), bookId);
        return ResponseEntity.ok(MessageResponse.of("Book removed from favorites"));
    }

    @GetMapping("/{bookId}/check")
    @Operation(summary = "Check if book is in favorites")
    public ResponseEntity<Boolean> checkFavorite(@PathVariable Long bookId) {
        boolean isFavorite = favoriteService.isFavorite(getCurrentUserId(), bookId);
        return ResponseEntity.ok(isFavorite);
    }
}