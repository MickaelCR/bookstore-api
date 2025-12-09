package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.jbnu.cr.bookstore.dto.response.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@Tag(name = "Health", description = "Health check API")
public class HealthController {

    private final DataSource dataSource;

    @Value("${app.version:1.0.0}")
    private String version;

    @Value("${app.build-time:unknown}")
    private String buildTime;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<HealthResponse> health() {
        boolean dbConnected = checkDatabaseConnection();
        return ResponseEntity.ok(HealthResponse.healthy(version, buildTime, dbConnected));
    }

    private boolean checkDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(1);
        } catch (Exception e) {
            return false;
        }
    }
}