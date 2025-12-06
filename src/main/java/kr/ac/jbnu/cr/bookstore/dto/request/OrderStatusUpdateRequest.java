package kr.ac.jbnu.cr.bookstore.dto.request;

import jakarta.validation.constraints.NotNull;
import kr.ac.jbnu.cr.bookstore.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}
