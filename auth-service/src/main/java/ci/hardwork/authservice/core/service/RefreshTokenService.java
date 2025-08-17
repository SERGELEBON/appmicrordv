package ci.hardwork.authservice.core.service;

import ci.hardwork.authservice.core.models.RefreshToken;
import ci.hardwork.authservice.core.models.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByUser(User user);
    void deleteExpiredTokens();
}