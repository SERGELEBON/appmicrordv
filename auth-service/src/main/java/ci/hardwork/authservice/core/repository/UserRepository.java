package ci.hardwork.authservice.core.repository;

import ci.hardwork.authservice.core.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    
    // Méthodes pour la gestion des médecins
    List<User> findByValidationStatus(User.ValidationStatus status);
    
    @Query("SELECT u FROM User u JOIN u.authorities a WHERE a.authority = 'MEDECIN' AND u.validationStatus = :status")
    List<User> findDoctorsByValidationStatus(User.ValidationStatus status);
    
    @Query("SELECT COUNT(u) FROM User u JOIN u.authorities a WHERE a.authority = 'MEDECIN' AND u.validationStatus = 'PENDING'")
    long countPendingDoctors();
}
