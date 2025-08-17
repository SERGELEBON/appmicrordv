package ci.hardwork.authservice.security;

import ci.hardwork.authservice.core.models.User;
import ci.hardwork.authservice.core.models.UserAuthority;
import ci.hardwork.authservice.core.models.enums.AuthorityEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Tests unitaires JwtUtils")
class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private User user;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // Set properties via reflection for testing
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "dGVzdFNlY3JldEtleTEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MA==");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 300000);

        // Setup user
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        UserAuthority authority = new UserAuthority();
        authority.setAuthority(AuthorityEnum.PATIENT);
        user.setAuthorities(List.of(authority));

        // Setup authentication
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    @DisplayName("Doit générer un token JWT valide")
    void shouldGenerateJwtToken_WhenValidAuthentication() {
        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertThat(token).isNotBlank();
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    @DisplayName("Doit générer un token à partir du nom d'utilisateur")
    void shouldGenerateTokenFromUsername_WhenValidUsername() {
        // When
        String token = jwtUtils.generateTokenFromUsername("testuser");

        // Then
        assertThat(token).isNotBlank();
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Doit extraire le nom d'utilisateur du token")
    void shouldGetUsernameFromToken_WhenValidToken() {
        // Given
        String token = jwtUtils.generateTokenFromUsername("testuser");

        // When
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Then
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Doit valider un token JWT correct")
    void shouldValidateToken_WhenTokenIsValid() {
        // Given
        String token = jwtUtils.generateTokenFromUsername("testuser");

        // When
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Doit rejeter un token JWT malformé")
    void shouldRejectToken_WhenTokenIsMalformed() {
        // Given
        String malformedToken = "malformed.jwt.token";

        // When
        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Doit rejeter un token JWT vide")
    void shouldRejectToken_WhenTokenIsEmpty() {
        // When
        boolean isValid1 = jwtUtils.validateJwtToken("");
        boolean isValid2 = jwtUtils.validateJwtToken(null);

        // Then
        assertThat(isValid1).isFalse();
        assertThat(isValid2).isFalse();
    }

    @Test
    @DisplayName("Doit détecter un token expiré")
    void shouldDetectExpiredToken_WhenTokenIsExpired() {
        // Given - Create JWT utils with very short expiration (1ms)
        JwtUtils shortExpirationJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "jwtSecret", "dGVzdFNlY3JldEtleTEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MA==");
        ReflectionTestUtils.setField(shortExpirationJwtUtils, "jwtExpirationMs", 1);

        String token = shortExpirationJwtUtils.generateTokenFromUsername("testuser");
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        boolean isValid = shortExpirationJwtUtils.validateJwtToken(token);
        boolean isExpired = jwtUtils.isTokenExpired(token);

        // Then
        assertThat(isValid).isFalse();
        assertThat(isExpired).isTrue();
    }

    @Test
    @DisplayName("Doit détecter qu'un token valide n'est pas expiré")
    void shouldDetectValidToken_WhenTokenIsNotExpired() {
        // Given
        String token = jwtUtils.generateTokenFromUsername("testuser");

        // When
        boolean isExpired = jwtUtils.isTokenExpired(token);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("Doit détecter qu'un token malformé est considéré comme expiré")
    void shouldConsiderMalformedTokenAsExpired() {
        // Given
        String malformedToken = "malformed.token";

        // When
        boolean isExpired = jwtUtils.isTokenExpired(malformedToken);

        // Then
        assertThat(isExpired).isTrue();
    }
}