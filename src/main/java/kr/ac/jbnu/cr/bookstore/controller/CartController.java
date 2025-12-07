package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.ac.jbnu.cr.bookstore.dto.request.CartItemRequest;
import kr.ac.jbnu.cr.bookstore.dto.request.CartItemUpdateRequest;
import kr.ac.jbnu.cr.bookstore.dto.response.CartResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.MessageResponse;
import kr.ac.jbnu.cr.bookstore.model.Cart;
import kr.ac.jbnu.cr.bookstore.security.JwtAuthentication;
import kr.ac.jbnu.cr.bookstore.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Tag(name = "Cart", description = "Shopping cart API")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private Long getCurrentUserId() {
        JwtAuthentication auth = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return auth.getUserId();
    }

    @GetMapping
    @Operation(summary = "Get current user's cart")
    public ResponseEntity<CartResponse> getCart() {
        Cart cart = cartService.getOrCreateCart(getCurrentUserId());
        return ResponseEntity.ok(CartResponse.from(cart));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody CartItemRequest request) {
        Cart cart = cartService.addItem(getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CartResponse.from(cart));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemUpdateRequest request) {
        Cart cart = cartService.updateItem(getCurrentUserId(), itemId, request);
        return ResponseEntity.ok(CartResponse.from(cart));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long itemId) {
        Cart cart = cartService.removeItem(getCurrentUserId(), itemId);
        return ResponseEntity.ok(CartResponse.from(cart));
    }

    @DeleteMapping
    @Operation(summary = "Clear all items from cart")
    public ResponseEntity<MessageResponse> clearCart() {
        cartService.clearCart(getCurrentUserId());
        return ResponseEntity.ok(MessageResponse.of("Cart cleared successfully"));
    }
}