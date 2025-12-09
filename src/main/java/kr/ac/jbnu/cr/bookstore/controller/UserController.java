package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.ac.jbnu.cr.bookstore.dto.request.UserUpdateRequest;
import kr.ac.jbnu.cr.bookstore.dto.response.ErrorResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.UserResponse;
import kr.ac.jbnu.cr.bookstore.model.User;
import kr.ac.jbnu.cr.bookstore.security.JwtAuthentication;
import kr.ac.jbnu.cr.bookstore.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management API")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> getCurrentUser() {
        JwtAuthentication auth = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findById(auth.getUserId());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        JwtAuthentication auth = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.update(auth.getUserId(), request);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}