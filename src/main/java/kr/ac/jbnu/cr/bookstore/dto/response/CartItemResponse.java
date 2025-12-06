package kr.ac.jbnu.cr.bookstore.dto.response;

import kr.ac.jbnu.cr.bookstore.model.CartItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CartItemResponse {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;

    public static CartItemResponse from(CartItem item) {
        BigDecimal unitPrice = item.getBook().getPrice();
        return CartItemResponse.builder()
                .id(item.getId())
                .bookId(item.getBook().getId())
                .bookTitle(item.getBook().getTitle())
                .bookAuthor(item.getBook().getAuthor())
                .unitPrice(unitPrice)
                .quantity(item.getQuantity())
                .subtotal(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}