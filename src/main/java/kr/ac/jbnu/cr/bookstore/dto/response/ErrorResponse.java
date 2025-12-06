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
    private String type = "about:blank";

    private String title;

    private int status;

    private String detail;

    private String instance;

    private Map<String, Object> errors;

    private String requestId;

    @Builder.Default
    private Instant timestamp = Instant.now();
}