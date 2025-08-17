package ci.hardwork.authservice.service;

import ci.hardwork.authservice.core.models.RefreshToken;
import ci.hardwork.authservice.core.models.User;
import ci.hardwork.authservice.core.repository.RefreshTokenRepository;
import ci.hardwork.authservice.core.repository.UserRepository;
import ci.hardwork.authservice.core.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires RefreshTokenService")
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        // Set refresh token expiration via reflection
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 604800000L);
        
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(3600));
    }

    @Test
    @DisplayName("Doit créer un refresh token pour un utilisateur")
    void shouldCreateRefreshToken_WhenValidUser() {
        // Given
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // When
        RefreshToken result = refreshTokenService.createRefreshToken(user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getToken()).isNotBlank();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Doit créer un refresh token même avec un utilisateur null - gestion dans le service")
    void shouldHandleNullUser() {
        // Given
        User nullUser = null;

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.createRefreshToken(nullUser))
                .isInstanceOf(NullPointerException.class);
        
        // Note: Le service peut sauvegarder avant de lever l'exception
    }

    @Test
    @DisplayName("Doit trouver un refresh token par token string")
    void shouldFindByToken_WhenTokenExists() {
        // Given
        String tokenValue = "test-token";
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(refreshToken));

        // When
        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenValue);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("Doit retourner vide si le token n'existe pas")
    void shouldReturnEmpty_WhenTokenNotFound() {
        // Given
        String tokenValue = "nonexistent-token";
        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        // When
        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenValue);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Doit vérifier qu'un token n'est pas expiré")
    void shouldVerifyExpiration_WhenTokenNotExpired() {
        // Given
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(3600)); // 1 hour in future

        // When
        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

        // Then
        assertThat(result).isEqualTo(refreshToken);
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Doit lever une exception si le token est expiré")
    void shouldThrowException_WhenTokenExpired() {
        // Given
        refreshToken.setExpiryDate(LocalDateTime.now().minusSeconds(3600)); // 1 hour in past

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(refreshToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Refresh token was expired. Please make a new signin request");
        
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    @DisplayName("Doit supprimer un refresh token par utilisateur")
    void shouldDeleteByUser_WhenUserExists() {
        // Given
        // When
        refreshTokenService.deleteByUser(user);

        // Then
        verify(refreshTokenRepository).deleteByUser(user);
    }

    @Test
    @DisplayName("Doit gérer la suppression avec un utilisateur null")
    void shouldHandleNullUserDeletion() {
        // Given
        User nullUser = null;

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.deleteByUser(nullUser))
                .isInstanceOf(Exception.class);
        
        verify(refreshTokenRepository, never()).deleteByUser(any(User.class));
    }

    @Test
    @DisplayName("Doit supprimer les tokens expirés")
    void shouldDeleteExpiredTokens() {
        // Given
        doNothing().when(refreshTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));

        // When
        refreshTokenService.deleteExpiredTokens();

        // Then
        verify(refreshTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }
}