package kr.ac.jbnu.cr.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.ac.jbnu.cr.bookstore.dto.request.UserUpdateRequest;
import kr.ac.jbnu.cr.bookstore.dto.response.PageResponse;
import kr.ac.jbnu.cr.bookstore.dto.response.UserResponse;
import kr.ac.jbnu.cr.bookstore.model.User;
import kr.ac.jbnu.cr.bookstore.security.JwtAuthentication;
import kr.ac.jbnu.cr.bookstore.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal JwtAuthentication auth) {
        User user = userService.findById(auth.getUserId());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @AuthenticationPrincipal JwtAuthentication auth,
            @Valid @RequestBody UserUpdateRequest request) {
        User user = userService.update(auth.getUserId(), request);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}