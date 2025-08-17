package ci.hardwork.gestionrdvservice.core.service;

import ci.hardwork.gestionrdvservice.core.dto.UserReference;

import java.util.Optional;

public interface UserReferenceService {
    Optional<UserReference> getUserById(Long userId);
    Optional<UserReference> getUserByEmail(String email);
    boolean userExists(Long userId);
}