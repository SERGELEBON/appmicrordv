package ci.hardwork.authservice.core.service;

import ci.hardwork.authservice.core.dto.RegisterRequest;
import ci.hardwork.authservice.core.dto.UserDto;
import ci.hardwork.authservice.core.models.User;
import ci.hardwork.authservice.core.models.enums.ActionEnum;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    UserDto createUser(RegisterRequest registerRequest);
    Optional<UserDto> findByUsername(String username);
    Optional<UserDto> findByEmail(String email);
    Optional<UserDto> findById(Long userId);
    List<UserDto> findAllUsers();
    void deleteUser(Long userId);
    UserDto updateUser(Long userId, UserDto userDto);
    void logUserAction(String username, ActionEnum action, HttpServletRequest request);
    void promoteUserToDoctor(Long userId);
    
    // Méthodes pour la validation des médecins
    List<UserDto> getPendingDoctors();
    UserDto approveDoctorValidation(Long userId, Long validatedBy);
    UserDto rejectDoctorValidation(Long userId, String rejectionReason, Long validatedBy);
    long getPendingDoctorsCount();
}
