package ci.hardwork.authservice.service;

import ci.hardwork.authservice.core.dto.RegisterRequest;
import ci.hardwork.authservice.core.dto.UserDto;
import ci.hardwork.authservice.core.mapper.UserMapper;
import ci.hardwork.authservice.core.models.User;
import ci.hardwork.authservice.core.models.UserAuthority;
import ci.hardwork.authservice.core.models.enums.ActionEnum;
import ci.hardwork.authservice.core.models.enums.AuthorityEnum;
import ci.hardwork.authservice.core.repository.UserAuthorityRepository;
import ci.hardwork.authservice.core.repository.UserHistoryRepository;
import ci.hardwork.authservice.core.repository.UserRepository;
import ci.hardwork.authservice.core.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserAuthorityRepository userAuthorityRepository;
    
    @Mock
    private UserHistoryRepository userHistoryRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("Test");
        user.setLastName("User");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
    }

    @Test
    @DisplayName("Doit créer un utilisateur avec succès")
    void shouldCreateUser_WhenValidData() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(RegisterRequest.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        // When
        UserDto result = userService.createUser(registerRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        
        verify(userRepository).save(any(User.class));
        verify(userAuthorityRepository).save(any(UserAuthority.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("Doit lever une exception si le nom d'utilisateur existe déjà")
    void shouldThrowException_WhenUsernameAlreadyExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username is already taken!");
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Doit lever une exception si l'email existe déjà")
    void shouldThrowException_WhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email is already in use!");
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Doit retourner un utilisateur par nom d'utilisateur")
    void shouldFindUserByUsername_WhenUserExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        Optional<UserDto> result = userService.findByUsername("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Doit retourner vide si l'utilisateur n'existe pas")
    void shouldReturnEmpty_WhenUserNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<UserDto> result = userService.findByUsername("nonexistent");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Doit charger les détails de l'utilisateur pour l'authentification")
    void shouldLoadUserByUsername_WhenUserExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        UserDetails result = userService.loadUserByUsername("testuser");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("Doit lever UsernameNotFoundException si l'utilisateur n'existe pas")
    void shouldThrowUsernameNotFoundException_WhenUserNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User Not Found: nonexistent");
    }

    @Test
    @DisplayName("Doit mettre à jour un utilisateur existant")
    void shouldUpdateUser_WhenUserExists() {
        // Given
        Long userId = 1L;
        UserDto updateDto = new UserDto();
        updateDto.setFirstName("Updated");
        updateDto.setLastName("Name");
        updateDto.setEmail("updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        // When
        UserDto result = userService.updateUser(userId, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(user);
        assertThat(user.getFirstName()).isEqualTo("Updated");
        assertThat(user.getLastName()).isEqualTo("Name");
        assertThat(user.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Doit supprimer un utilisateur existant")
    void shouldDeleteUser_WhenUserExists() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Doit lever une exception lors de la suppression d'un utilisateur inexistant")
    void shouldThrowException_WhenDeletingNonExistentUser() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
        
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Doit enregistrer l'action de l'utilisateur")
    void shouldLogUserAction_WhenUserExists() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn("Test Agent");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        userService.logUserAction("testuser", ActionEnum.LOGIN, request);

        // Then
        verify(userHistoryRepository).save(any());
    }
}