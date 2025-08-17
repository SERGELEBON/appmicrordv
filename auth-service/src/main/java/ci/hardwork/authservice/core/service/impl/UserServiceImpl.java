package ci.hardwork.authservice.core.service.impl;

import ci.hardwork.authservice.core.dto.*;
import ci.hardwork.authservice.core.mapper.UserMapper;
import ci.hardwork.authservice.core.models.User;
import ci.hardwork.authservice.core.models.UserAuthority;
import ci.hardwork.authservice.core.models.UserHistory;
import ci.hardwork.authservice.core.models.enums.ActionEnum;
import ci.hardwork.authservice.core.models.enums.AuthorityEnum;
import ci.hardwork.authservice.core.repository.UserAuthorityRepository;
import ci.hardwork.authservice.core.repository.UserHistoryRepository;
import ci.hardwork.authservice.core.repository.UserRepository;
import ci.hardwork.authservice.core.service.UserService;
import ci.hardwork.authservice.core.services.TwilioEmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TwilioEmailService twilioEmailService;

    @PostConstruct
    public void initDefaultAdmin() {
        // Créer un admin par défaut si aucun n'existe
        if (!userRepository.existsByUsername("admin")) {
            try {
                log.info("Creating default admin user...");
                
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@rdv360.com");
                admin.setFirstName("Admin");
                admin.setLastName("RDV360");
                admin.setPassword(passwordEncoder.encode("admin123")); // Mot de passe par défaut
                admin.setIsEnabled(true);
                admin.setEmailVerified(true);
                
                admin = userRepository.save(admin);
                
                // Assigner le rôle ADMIN
                UserAuthority adminAuthority = new UserAuthority();
                adminAuthority.setUser(admin);
                adminAuthority.setAuthority(AuthorityEnum.ADMIN);
                userAuthorityRepository.save(adminAuthority);
                
                log.info("Default admin user created successfully - Username: admin, Password: admin123");
            } catch (Exception e) {
                log.error("Failed to create default admin user", e);
            }
        }
    }

    @Override
    public UserDto createUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        
        user = userRepository.save(user);

        // Déterminer le rôle en fonction du type d'utilisateur
        AuthorityEnum authority;
        if ("MEDECIN".equals(registerRequest.getUserType())) {
            // Pour les médecins : désactiver le compte et mettre en attente de validation
            user.setIsEnabled(false);
            user.setSpeciality(registerRequest.getSpeciality());
            user.setLicenseNumber(registerRequest.getLicenseNumber());
            user.setValidationStatus(User.ValidationStatus.PENDING);
            authority = AuthorityEnum.MEDECIN; // Rôle médecin mais compte désactivé
            
            // Envoyer email de demande via Twilio (optionnel)
            try {
                twilioEmailService.sendDoctorValidationRequestEmail(user);
            } catch (Exception e) {
                // Ne pas échouer l'inscription si l'email échoue
                log.warn("Failed to send doctor validation email", e);
            }
        } else {
            // Pour les patients, accès immédiat
            authority = AuthorityEnum.PATIENT;
            
            // Envoyer email de bienvenue via Twilio (optionnel)
            try {
                twilioEmailService.sendWelcomePatientEmail(
                    registerRequest.getEmail(),
                    registerRequest.getFirstName(),
                    registerRequest.getLastName()
                );
            } catch (Exception e) {
                // Ne pas échouer l'inscription si l'email échoue
                log.warn("Failed to send welcome email", e);
            }
        }
        
        // Sauvegarder l'utilisateur avec ses modifications
        user = userRepository.save(user);

        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setUser(user);
        userAuthority.setAuthority(authority);
        userAuthorityRepository.save(userAuthority);

        return userMapper.toDto(user);
    }

    @Override
    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto);
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto);
    }

    @Override
    public Optional<UserDto> findById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public void logUserAction(String username, ActionEnum action, HttpServletRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            UserHistory history = new UserHistory();
            history.setUser(userOpt.get());
            history.setAction(action);
            history.setIpAddress(getClientIpAddress(request));
            history.setUserAgent(request.getHeader("User-Agent"));
            userHistoryRepository.save(history);
        }
    }

    @Override
    public void promoteUserToDoctor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Vérifier si l'utilisateur a déjà le rôle MEDECIN
        boolean hasDoctorRole = userAuthorityRepository.findByUser(user)
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals(AuthorityEnum.MEDECIN.name()));
        
        if (!hasDoctorRole) {
            // Ajouter le rôle MEDECIN (sans supprimer PATIENT pour éviter les problèmes)
            UserAuthority doctorAuthority = new UserAuthority();
            doctorAuthority.setUser(user);
            doctorAuthority.setAuthority(AuthorityEnum.MEDECIN);
            userAuthorityRepository.save(doctorAuthority);
            
            // Optionnel: supprimer le rôle PATIENT si nécessaire
            userAuthorityRepository.findByUser(user)
                    .stream()
                    .filter(auth -> auth.getAuthority().equals(AuthorityEnum.PATIENT.name()))
                    .forEach(userAuthorityRepository::delete);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Chercher d'abord par username, puis par email si non trouvé
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));
        return user;
    }

    @Override
    public List<UserDto> getPendingDoctors() {
        List<User> pendingDoctors = userRepository.findDoctorsByValidationStatus(User.ValidationStatus.PENDING);
        return pendingDoctors.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto approveDoctorValidation(Long userId, Long validatedBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getValidationStatus() != User.ValidationStatus.PENDING) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }
        
        // Activer le compte et approuver
        user.setIsEnabled(true);
        user.setValidationStatus(User.ValidationStatus.APPROVED);
        user.setValidationDate(java.time.LocalDateTime.now());
        user.setValidatedBy(validatedBy);
        
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto rejectDoctorValidation(Long userId, String rejectionReason, Long validatedBy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getValidationStatus() != User.ValidationStatus.PENDING) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }
        
        // Rejeter la demande
        user.setValidationStatus(User.ValidationStatus.REJECTED);
        user.setValidationDate(java.time.LocalDateTime.now());
        user.setValidatedBy(validatedBy);
        user.setRejectionReason(rejectionReason);
        
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public long getPendingDoctorsCount() {
        return userRepository.countPendingDoctors();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}