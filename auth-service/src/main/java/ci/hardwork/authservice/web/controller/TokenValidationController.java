package ci.hardwork.authservice.web.controller;

import ci.hardwork.authservice.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenValidationController {

    private final JwtUtils jwtUtils;

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }

            String token = authHeader.substring(7);
            
            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(401).build();
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("username", username);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}