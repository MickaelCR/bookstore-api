package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.jbnu.cr.bookstore.dto.response.ErrorResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.StatsResponse;
import kr.ac.jbnu.cr.bookstore.model.OrderStatus;
import kr.ac.jbnu.cr.bookstore.repository.BookRepository;
import kr.ac.jbnu.cr.bookstore.repository.ReviewRepository;
import kr.ac.jbnu.cr.bookstore.repository.UserRepository;
import kr.ac.jbnu.cr.bookstore.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stats")
@Tag(name = "Admin - Statistics", description = "Admin statistics API")
public class AdminStatsController {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final OrderService orderService;

    public AdminStatsController(UserRepository userRepository,
                                BookRepository bookRepository,
                                ReviewRepository reviewRepository,
                                OrderService orderService) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
        this.orderService = orderService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get summary statistics (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<StatsResponse> getSummaryStats() {
        StatsResponse stats = StatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalBooks(bookRepository.count())
                .totalOrders(orderService.countByStatus(OrderStatus.CREATED) +
                        orderService.countByStatus(OrderStatus.PAID) +
                        orderService.countByStatus(OrderStatus.SHIPPED) +
                        orderService.countByStatus(OrderStatus.DELIVERED))
                .todayOrders(orderService.countTodayOrders())
                .totalSales(orderService.getTotalSales())
                .todaySales(orderService.getTodaySales())
                .pendingOrders(orderService.countByStatus(OrderStatus.CREATED))
                .totalReviews(reviewRepository.count())
                .build();

        return ResponseEntity.ok(stats);
    }
}