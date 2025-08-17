package ci.hardwork.authservice.web.controller;

import ci.hardwork.authservice.core.dto.*;
import ci.hardwork.authservice.core.models.RefreshToken;
import ci.hardwork.authservice.core.models.User;
import ci.hardwork.authservice.core.models.enums.ActionEnum;
import ci.hardwork.authservice.core.service.RefreshTokenService;
import ci.hardwork.authservice.core.service.UserService;
import ci.hardwork.authservice.core.repository.UserRepository;
import ci.hardwork.authservice.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                            HttpServletRequest request) {
        log.info("Login attempt for user: {}", loginRequest.getEmail());

        try {
            // Utiliser l'email comme username pour l'authentification
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities() != null ? 
                    userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList() : 
                    List.of();

            // Create refresh token
            User user = (User) userDetails;
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            userService.logUserAction(loginRequest.getEmail(), ActionEnum.LOGIN, request);
            log.info("Login successful for user: {}", loginRequest.getEmail());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    refreshToken.getToken(),
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    roles));
        } catch (org.springframework.security.authentication.DisabledException e) {
            log.warn("Authentication failed for user '{}' since their account is disabled", loginRequest.getEmail());
            
            // Vérifier si c'est un médecin en attente de validation
            return userService.findByEmail(loginRequest.getEmail())
                    .map(userDto -> {
                        if (userDto.getValidationStatus() != null && "PENDING".equals(userDto.getValidationStatus().toString())) {
                            return ResponseEntity.status(403)
                                    .body(new MessageResponse("Votre compte médecin est en attente de validation. Vous recevrez un email une fois votre compte approuvé par l'administration."));
                        } else if (userDto.getValidationStatus() != null && "REJECTED".equals(userDto.getValidationStatus().toString())) {
                            return ResponseEntity.status(403)
                                    .body(new MessageResponse("Votre demande de validation médecin a été rejetée. Contactez l'administration ou soumettez une nouvelle demande."));
                        } else {
                            return ResponseEntity.status(403)
                                    .body(new MessageResponse("Votre compte a été désactivé. Contactez l'administration."));
                        }
                    })
                    .orElse(ResponseEntity.status(403)
                            .body(new MessageResponse("Compte désactivé. Contactez l'administration.")));
        }
    }

    @Operation(summary = "User registration", description = "Register a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration successful",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Username or email already exists",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest,
                                        HttpServletRequest request) {
        log.info("Registration attempt for user: {}", signUpRequest.getUsername());
        try {
            userService.createUser(signUpRequest);
            userService.logUserAction(signUpRequest.getUsername(), ActionEnum.REGISTER, request);
            log.info("Registration successful for user: {}", signUpRequest.getUsername());
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (RuntimeException e) {
            log.error("Registration failed for user: {} - Error: {}", signUpRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @Operation(summary = "User logout", description = "Log out current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
    })
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            userService.logUserAction(userDetails.getUsername(), ActionEnum.LOGOUT, request);
            log.info("Logout successful for user: {}", userDetails.getUsername());
        } else {
            log.warn("Logout attempt without authentication");
        }
        return ResponseEntity.ok(new MessageResponse("You've been signed out!"));
    }

    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode = "403", description = "Refresh token expired or invalid")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    List<String> authorities = user.getAuthorities() != null ? 
                        user.getAuthorities().stream().map(auth -> auth.getAuthority()).toList() : 
                        List.of();
                    return ResponseEntity.ok(new JwtResponse(token, requestRefreshToken, 
                            user.getId(), user.getUsername(), user.getEmail(), authorities));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @Operation(summary = "Validate token", description = "Validate JWT token for other microservices")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token validation result")
    })
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@Valid @RequestBody TokenValidationRequest request) {
        String token = request.getToken();
        
        try {
            if (jwtUtils.validateJwtToken(token)) {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                
                List<String> authorities = user.getAuthorities() != null ? 
                    user.getAuthorities().stream().map(auth -> auth.getAuthority()).toList() : 
                    List.of();
                    
                return ResponseEntity.ok(new TokenValidationResponse(
                    true,
                    user.getUsername(),
                    user.getEmail(),
                    authorities,
                    user.getId()
                ));
            } else {
                return ResponseEntity.ok(new TokenValidationResponse(
                    false, null, null, null, null
                ));
            }
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return ResponseEntity.ok(new TokenValidationResponse(
                false, null, null, null, null
            ));
        }
    }
}