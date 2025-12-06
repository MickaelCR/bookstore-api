package kr.ac.jbnu.cr.bookstore.service;

import kr.ac.jbnu.cr.bookstore.exception.BadRequestException;
import kr.ac.jbnu.cr.bookstore.exception.ForbiddenException;
import kr.ac.jbnu.cr.bookstore.exception.ResourceNotFoundException;
import kr.ac.jbnu.cr.bookstore.exception.StateConflictException;
import kr.ac.jbnu.cr.bookstore.model.*;
import kr.ac.jbnu.cr.bookstore.repository.CartRepository;
import kr.ac.jbnu.cr.bookstore.repository.OrderRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Order testOrder;
    private Cart testCart;
    private Book testBook;

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
                .price(BigDecimal.valueOf(29.99))
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .book(testBook)
                .quantity(2)
                .build();

        testCart = Cart.builder()
                .id(1L)
                .user(testUser)
                .items(new ArrayList<>(List.of(cartItem)))
                .build();

        testOrder = Order.builder()
                .id(1L)
                .user(testUser)
                .totalAmount(BigDecimal.valueOf(59.98))
                .status(OrderStatus.CREATED)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Find by ID - Success")
    void findById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Order result = orderService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Find by ID - Not found throws exception")
    void findById_NotFound_ThrowsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Find by ID for user - Forbidden for other user")
    void findByIdForUser_Forbidden() {
        User otherUser = User.builder().id(2L).build();
        testOrder.setUser(otherUser);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThatThrownBy(() -> orderService.findByIdForUser(1L, 1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Create from cart - Empty cart throws exception")
    void createFromCart_EmptyCart_ThrowsException() {
        testCart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        assertThatThrownBy(() -> orderService.createFromCart(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cart is empty");
    }

    @Test
    @DisplayName("Cancel - Success")
    void cancel_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.cancel(1L, 1L);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Cancel - Already shipped throws exception")
    void cancel_AlreadyShipped_ThrowsException() {
        testOrder.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThatThrownBy(() -> orderService.cancel(1L, 1L))
                .isInstanceOf(StateConflictException.class);
    }

    @Test
    @DisplayName("Update status - Invalid transition throws exception")
    void updateStatus_InvalidTransition_ThrowsException() {
        testOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThatThrownBy(() -> orderService.updateStatus(1L, OrderStatus.CREATED))
                .isInstanceOf(StateConflictException.class);
    }
}