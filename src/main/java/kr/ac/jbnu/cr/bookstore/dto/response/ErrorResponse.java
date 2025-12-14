package kr.ac.jbnu.cr.bookstore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    @Builder.Default
    private Instant timestamp = Instant.now();
    private String path;
    private int status;
    private String code;
    private String message;
    private Map<String, Object> details;
}