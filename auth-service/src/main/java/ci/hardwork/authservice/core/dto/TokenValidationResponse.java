package ci.hardwork.authservice.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TokenValidationResponse {
    private boolean valid;
    private String username;
    private String email;
    private List<String> authorities;
    private Long userId;
}