package kr.ac.jbnu.cr.bookstore.dto.response;

import kr.ac.jbnu.cr.bookstore.model.Cart;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CartResponse {

    private Long id;
    private List<CartItemResponse> items;
    private int totalItems;
    private BigDecimal totalAmount;
    private LocalDateTime updatedAt;

    public static CartResponse from(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = cart.getItems().stream()
                .mapToInt(item -> item.getQuantity())
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}