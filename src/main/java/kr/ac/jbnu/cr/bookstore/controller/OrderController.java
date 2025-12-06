package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.jbnu.cr.bookstore.dto.response.OrderResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.PageResponse;
import kr.ac.jbnu.cr.bookstore.model.Order;
import kr.ac.jbnu.cr.bookstore.security.JwtAuthentication;
import kr.ac.jbnu.cr.bookstore.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order management API")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get my orders")
    public ResponseEntity<PageResponse<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal JwtAuthentication auth,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Order> orders = orderService.findByUserId(auth.getUserId(), pageable);

        List<OrderResponse> content = orders.getContent().stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(PageResponse.of(orders, content));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrderById(
            @AuthenticationPrincipal JwtAuthentication auth,
            @PathVariable Long id) {

        Order order = orderService.findByIdForUser(auth.getUserId(), id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @PostMapping
    @Operation(summary = "Create order from cart")
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal JwtAuthentication auth) {
        Order order = orderService.createFromCart(auth.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel order")
    public ResponseEntity<OrderResponse> cancelOrder(
            @AuthenticationPrincipal JwtAuthentication auth,
            @PathVariable Long id) {

        Order order = orderService.cancel(auth.getUserId(), id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}