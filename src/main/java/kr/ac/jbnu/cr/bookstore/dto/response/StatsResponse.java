package kr.ac.jbnu.cr.bookstore.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class StatsResponse {

    private long totalUsers;
    private long totalBooks;
    private long totalOrders;
    private long todayOrders;
    private BigDecimal totalSales;
    private BigDecimal todaySales;
    private long pendingOrders;
    private long totalReviews;
}