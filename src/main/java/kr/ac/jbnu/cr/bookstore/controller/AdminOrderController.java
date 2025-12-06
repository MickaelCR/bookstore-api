package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.ac.jbnu.cr.bookstore.dto.request.OrderStatusUpdateRequest;
import kr.ac.jbnu.cr.bookstore.dto.response.OrderResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.PageResponse;
import kr.ac.jbnu.cr.bookstore.model.Order;
import kr.ac.jbnu.cr.bookstore.model.OrderStatus;
import kr.ac.jbnu.cr.bookstore.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/orders")
@Tag(name = "Admin - Orders", description = "Admin order management API")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get all orders (admin only)")
    public ResponseEntity<PageResponse<OrderResponse>> getAllOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) OrderStatus status) {

        Page<Order> orders;
        if (status != null) {
            orders = orderService.findByStatus(status, pageable);
        } else {
            orders = orderService.findAll(pageable);
        }

        List<OrderResponse> content = orders.getContent().stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(PageResponse.of(orders, content));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID (admin only)")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status (admin only)")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {

        Order order = orderService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}
