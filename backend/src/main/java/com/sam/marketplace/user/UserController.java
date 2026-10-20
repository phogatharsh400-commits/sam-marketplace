package com.sam.marketplace.user;

import com.sam.marketplace.common.response.ApiResponse;
import com.sam.marketplace.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Profile", description = "Current Authenticated User Operations")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UserDto userDto = userService.getUserById(currentUserId);
        return ResponseEntity.ok(ApiResponse.success(userDto, "User profile retrieved successfully"));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current authenticated user profile")
    public ResponseEntity<ApiResponse<UserDto>> updateCurrentUser(@RequestBody UserDto updateDto) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UserDto userDto = userService.updateUserProfile(currentUserId, updateDto);
        return ResponseEntity.ok(ApiResponse.success(userDto, "User profile updated successfully"));
    }
}
