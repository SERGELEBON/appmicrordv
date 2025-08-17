package ci.hardwork.authservice.web.controller;

import ci.hardwork.authservice.core.dto.UserDto;
import ci.hardwork.authservice.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "Endpoints for user management operations")
public class UserController {
    
    private final UserService userService;

    @Operation(summary = "Get all users", description = "Retrieve all users (Admin only)")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Admin requesting all users");
        List<UserDto> users = userService.findAllUsers();
        log.info("Retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID", description = "Retrieve user by ID (Admin or own profile only)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("Requesting user with ID: {}", id);
        // TODO: Implement findById in UserService
        log.warn("getUserById not yet implemented");
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Update user", description = "Update user information (Admin or own profile only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Updating user with ID: {}", id);
        try {
            UserDto updatedUser = userService.updateUser(id, userDto);
            log.info("User updated successfully: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            log.error("Failed to update user {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete user", description = "Delete user account (Admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        try {
            userService.deleteUser(id);
            log.info("User deleted successfully: {}", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Failed to delete user {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}