package kr.ac.jbnu.cr.bookstore.service;

import kr.ac.jbnu.cr.bookstore.dto.request.CartItemRequest;
import kr.ac.jbnu.cr.bookstore.dto.request.CartItemUpdateRequest;
import kr.ac.jbnu.cr.bookstore.exception.ResourceNotFoundException;
import kr.ac.jbnu.cr.bookstore.model.Book;
import kr.ac.jbnu.cr.bookstore.model.Cart;
import kr.ac.jbnu.cr.bookstore.model.CartItem;
import kr.ac.jbnu.cr.bookstore.model.User;
import kr.ac.jbnu.cr.bookstore.repository.BookRepository;
import kr.ac.jbnu.cr.bookstore.repository.CartItemRepository;
import kr.ac.jbnu.cr.bookstore.repository.CartRepository;
import kr.ac.jbnu.cr.bookstore.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Book testBook;
    private Cart testCart;
    private CartItem testCartItem;
    private CartItemRequest cartItemRequest;
    private CartItemUpdateRequest cartItemUpdateRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .build();

        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .price(BigDecimal.valueOf(29.99))
                .stockQuantity(100)
                .isActive(true)
                .build();

        testCart = Cart.builder()
                .id(1L)
                .user(testUser)
                .items(new ArrayList<>())
                .build();

        testCartItem = CartItem.builder()
                .id(1L)
                .cart(testCart)
                .book(testBook)
                .quantity(2)
                .build();

        cartItemRequest = CartItemRequest.builder()
                .bookId(1L)
                .quantity(2)
                .build();

        cartItemUpdateRequest = CartItemUpdateRequest.builder()
                .quantity(5)
                .build();
    }

    @Test
    @DisplayName("Get or create cart - Returns existing cart")
    void getOrCreateCart_ReturnsExistingCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        Cart result = cartService.getOrCreateCart(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Get or create cart - Creates new cart when not exists")
    void getOrCreateCart_CreatesNewCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.getOrCreateCart(1L);

        assertThat(result).isNotNull();
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Get or create cart - User not found throws exception")
    void getOrCreateCart_UserNotFound_ThrowsException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getOrCreateCart(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Get cart - Success")
    void getCart_Success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        Cart result = cartService.getCart(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Get cart - Not found throws exception")
    void getCart_NotFound_ThrowsException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getCart(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Add item - New item success")
    void addItem_NewItem_Success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(bookRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testBook));
        when(cartItemRepository.findByCartIdAndBookId(1L, 1L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        Cart result = cartService.addItem(1L, cartItemRequest);

        assertThat(result).isNotNull();
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Add item - Existing item updates quantity")
    void addItem_ExistingItem_UpdatesQuantity() {
        testCart.getItems().add(testCartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(bookRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testBook));
        when(cartItemRepository.findByCartIdAndBookId(1L, 1L)).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        cartService.addItem(1L, cartItemRequest);

        assertThat(testCartItem.getQuantity()).isEqualTo(4);
    }

    @Test
    @DisplayName("Add item - Book not found throws exception")
    void addItem_BookNotFound_ThrowsException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(bookRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItem(1L, cartItemRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Update item - Success")
    void updateItem_Success() {
        testCart.getItems().add(testCartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        cartService.updateItem(1L, 1L, cartItemUpdateRequest);

        assertThat(testCartItem.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Update item - Item not found throws exception")
    void updateItem_ItemNotFound_ThrowsException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.updateItem(1L, 1L, cartItemUpdateRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Update item - Wrong cart throws exception")
    void updateItem_WrongCart_ThrowsException() {
        Cart otherCart = Cart.builder().id(2L).build();
        testCartItem.setCart(otherCart);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(testCartItem));

        assertThatThrownBy(() -> cartService.updateItem(1L, 1L, cartItemUpdateRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Remove item - Success")
    void removeItem_Success() {
        testCart.getItems().add(testCartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(testCartItem));
        doNothing().when(cartItemRepository).delete(any(CartItem.class));

        cartService.removeItem(1L, 1L);

        verify(cartItemRepository, times(1)).delete(testCartItem);
    }

    @Test
    @DisplayName("Remove item - Item not found throws exception")
    void removeItem_ItemNotFound_ThrowsException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.removeItem(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Clear cart - Success")
    void clearCart_Success() {
        testCart.getItems().add(testCartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        doNothing().when(cartItemRepository).deleteByCartId(1L);

        cartService.clearCart(1L);

        verify(cartItemRepository, times(1)).deleteByCartId(1L);
    }

    @Test
    @DisplayName("Is cart empty - Returns true for empty cart")
    void isCartEmpty_ReturnsTrue() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        boolean result = cartService.isCartEmpty(1L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Is cart empty - Returns false for non-empty cart")
    void isCartEmpty_ReturnsFalse() {
        testCart.getItems().add(testCartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        boolean result = cartService.isCartEmpty(1L);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Is cart empty - Returns true when cart not exists")
    void isCartEmpty_NoCart_ReturnsTrue() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        boolean result = cartService.isCartEmpty(1L);

        assertThat(result).isTrue();
    }
}