package ci.hardwork.gestionrdvservice.core.repository;

import ci.hardwork.gestionrdvservice.core.models.Medecin;
import ci.hardwork.gestionrdvservice.core.models.enums.SpecialiteMedicale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    
    Optional<Medecin> findByUserId(Long userId);
    
    Optional<Medecin> findByNumeroRPPS(String numeroRPPS);
    
    Optional<Medecin> findByEmail(String email);
    
    List<Medecin> findBySpecialite(SpecialiteMedicale specialite);
    
    List<Medecin> findByActifTrue();
    
    List<Medecin> findBySpecialiteAndActifTrue(SpecialiteMedicale specialite);
    
    @Query("SELECT m FROM Medecin m WHERE LOWER(m.nom) LIKE LOWER(CONCAT('%', :nom, '%')) AND m.actif = true")
    List<Medecin> findByNomContainingIgnoreCaseAndActiveTrue(@Param("nom") String nom);
    
    @Query("SELECT m FROM Medecin m WHERE LOWER(CONCAT(m.prenom, ' ', m.nom)) LIKE LOWER(CONCAT('%', :nomComplet, '%')) AND m.actif = true")
    List<Medecin> findByNomCompletContainingIgnoreCaseAndActiveTrue(@Param("nomComplet") String nomComplet);
    
    @Query("SELECT m FROM Medecin m WHERE LOWER(m.villeCabinet) LIKE LOWER(CONCAT('%', :ville, '%')) AND m.actif = true")
    List<Medecin> findByVilleCabinetContainingIgnoreCase(@Param("ville") String ville);
    
    @Query("SELECT m FROM Medecin m WHERE m.specialite = :specialite AND m.villeCabinet = :ville AND m.actif = true")
    List<Medecin> findBySpecialiteAndVilleCabinetAndActiveTrue(@Param("specialite") SpecialiteMedicale specialite, @Param("ville") String ville);
    
    boolean existsByUserId(Long userId);
    
    boolean existsByNumeroRPPS(String numeroRPPS);
    
    long countByActifTrue();
    
    boolean existsByEmail(String email);
    
    // Méthodes pour statistiques admin
    int countByDateCreationBetween(LocalDateTime debut, LocalDateTime fin);
    
    int countByDateCreationAfter(LocalDateTime since);
}