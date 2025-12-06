package kr.ac.jbnu.cr.bookstore.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class MessageResponse {

    private String message;

    @Builder.Default
    private Instant timestamp = Instant.now();

    public static MessageResponse of(String message) {
        return MessageResponse.builder()
                .message(message)
                .build();
    }
}