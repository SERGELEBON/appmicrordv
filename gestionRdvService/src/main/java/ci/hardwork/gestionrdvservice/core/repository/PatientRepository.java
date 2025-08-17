package ci.hardwork.gestionrdvservice.core.repository;

import ci.hardwork.gestionrdvservice.core.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByUserId(Long userId);
    
    Optional<Patient> findByNumeroSecuriteSociale(String numeroSecuriteSociale);
    
    Optional<Patient> findByEmail(String email);
    
    @Query("SELECT p FROM Patient p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    List<Patient> findByNomContainingIgnoreCase(@Param("nom") String nom);
    
    @Query("SELECT p FROM Patient p WHERE LOWER(p.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))")
    List<Patient> findByPrenomContainingIgnoreCase(@Param("prenom") String prenom);
    
    @Query("SELECT p FROM Patient p WHERE LOWER(CONCAT(p.prenom, ' ', p.nom)) LIKE LOWER(CONCAT('%', :nomComplet, '%'))")
    List<Patient> findByNomCompletContainingIgnoreCase(@Param("nomComplet") String nomComplet);
    
    boolean existsByUserId(Long userId);
    
    boolean existsByNumeroSecuriteSociale(String numeroSecuriteSociale);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT p FROM Patient p WHERE (LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR :nom IS NULL OR :nom = '') AND (LOWER(p.prenom) LIKE LOWER(CONCAT('%', :prenom, '%')) OR :prenom IS NULL OR :prenom = '')")
    List<Patient> findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(@Param("nom") String nom, @Param("prenom") String prenom);
    
    long countByActifTrue();
    
    // Méthodes pour statistiques admin
    int countByDateCreationBetween(LocalDateTime debut, LocalDateTime fin);
    
    int countByDateCreationAfter(LocalDateTime since);
}