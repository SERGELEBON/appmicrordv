package ci.hardwork.authservice.core.dto;

import ci.hardwork.authservice.core.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDto {
    
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isEnabled;
    private List<String> authorities;
    
    // Champs spécifiques aux médecins
    private String speciality;
    private String licenseNumber;
    private User.ValidationStatus validationStatus;
    private String rejectionReason;
    private LocalDateTime validationDate;
    private Long validatedBy;
    private Boolean emailVerified;
}
