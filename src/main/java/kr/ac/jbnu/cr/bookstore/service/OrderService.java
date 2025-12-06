package kr.ac.jbnu.cr.bookstore.service;

import kr.ac.jbnu.cr.bookstore.exception.BadRequestException;
import kr.ac.jbnu.cr.bookstore.exception.ForbiddenException;
import kr.ac.jbnu.cr.bookstore.exception.ResourceNotFoundException;
import kr.ac.jbnu.cr.bookstore.exception.StateConflictException;
import kr.ac.jbnu.cr.bookstore.model.*;
import kr.ac.jbnu.cr.bookstore.repository.CartRepository;
import kr.ac.jbnu.cr.bookstore.repository.OrderRepository;
import kr.ac.jbnu.cr.bookstore.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        UserRepository userRepository,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    /**
     * Find order by ID
     */
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    /**
     * Find order by ID for user
     */
    public Order findByIdForUser(Long userId, Long orderId) {
        Order order = findById(orderId);

        if (!order.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only view your own orders");
        }

        return order;
    }

    /**
     * Find orders by user
     */
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    /**
     * Find orders by status
     */
    public Page<Order> findByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    /**
     * Find all orders (admin)
     */
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /**
     * Create order from cart
     */
    @Transactional
    public Order createFromCart(Long userId) {
        // Get user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            BigDecimal itemTotal = cartItem.getBook().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // Create order
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.CREATED)
                .build();

        // Create order items from cart items
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .book(cartItem.getBook())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getBook().getPrice())
                    .totalPrice(cartItem.getBook().getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build();

            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // Clear cart after order
        cartService.clearCart(userId);

        return savedOrder;
    }

    /**
     * Update order status (admin only)
     */
    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = findById(orderId);

        // Validate status transition
        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    /**
     * Cancel order (user)
     */
    @Transactional
    public Order cancel(Long userId, Long orderId) {
        Order order = findByIdForUser(userId, orderId);

        // Can only cancel if status is CREATED
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new StateConflictException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    /**
     * Validate status transition
     */
    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case CREATED -> next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
            case PAID -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!valid) {
            throw new StateConflictException("Invalid status transition from " + current + " to " + next);
        }
    }

    /**
     * Get total sales
     */
    public BigDecimal getTotalSales() {
        BigDecimal total = orderRepository.getTotalSales();
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get today's sales
     */
    public BigDecimal getTodaySales() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        BigDecimal total = orderRepository.getSalesBetween(startOfDay, endOfDay);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Count orders by status
     */
    public long countByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    /**
     * Count today's orders
     */
    public long countTodayOrders() {
        return orderRepository.countTodayOrders();
    }
}