package kr.ac.jbnu.cr.bookstore.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class HealthResponse {

    private String status;
    private String version;
    private String buildTime;
    private Instant timestamp;
    private String database;

    public static HealthResponse healthy(String version, String buildTime, boolean dbConnected) {
        return HealthResponse.builder()
                .status("UP")
                .version(version)
                .buildTime(buildTime)
                .timestamp(Instant.now())
                .database(dbConnected ? "UP" : "DOWN")
                .build();
    }
}
