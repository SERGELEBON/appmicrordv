package ci.hardwork.gestionrdvservice.core.repository;

import ci.hardwork.gestionrdvservice.core.models.Medecin;
import ci.hardwork.gestionrdvservice.core.models.Patient;
import ci.hardwork.gestionrdvservice.core.models.RendezVous;
import ci.hardwork.gestionrdvservice.core.models.enums.RendezVousStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    
    List<RendezVous> findByPatient(Patient patient);
    
    List<RendezVous> findByMedecin(Medecin medecin);
    
    List<RendezVous> findByStatut(RendezVousStatus statut);
    
    List<RendezVous> findByPatientIdOrderByDateHeureDebutDesc(Long patientId);
    
    List<RendezVous> findByMedecinIdOrderByDateHeureDebut(Long medecinId);
    
    List<RendezVous> findByDateHeureDebutBetweenOrderByDateHeureDebut(LocalDateTime dateDebut, LocalDateTime dateFin);
    
    List<RendezVous> findByMedecinIdAndDateHeureDebutBetweenOrderByDateHeureDebut(Long medecinId, LocalDateTime dateDebut, LocalDateTime dateFin);
    
    List<RendezVous> findByPatientIdAndDateHeureDebutBetweenOrderByDateHeureDebut(Long patientId, LocalDateTime dateDebut, LocalDateTime dateFin);
    
    List<RendezVous> findByStatutOrderByDateHeureDebut(RendezVousStatus statut);
    
    long countByStatut(RendezVousStatus statut);
    
    @Query("SELECT r FROM RendezVous r WHERE r.medecin.id = :medecinId AND ((r.dateHeureDebut < :fin AND r.dateHeureFin > :debut) OR (r.dateHeureDebut <= :debut AND r.dateHeureFin >= :fin)) AND r.statut NOT IN ('ANNULE')")
    List<RendezVous> findConflictingRendezVous(@Param("medecinId") Long medecinId, @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT r FROM RendezVous r WHERE r.rappelEnvoye = false AND r.dateHeureDebut BETWEEN :debut AND :fin AND r.statut IN ('PLANIFIE', 'CONFIRME')")
    List<RendezVous> findRendezVousRequiringReminder(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
    
    // Méthodes pour statistiques admin
    int countByDateHeureDebutBetween(LocalDateTime debut, LocalDateTime fin);
    
    int countByDateCreationAfter(LocalDateTime since);
    
    @Query("SELECT r FROM RendezVous r ORDER BY r.dateCreation DESC LIMIT :limit")
    List<RendezVous> findTopByOrderByDateCreationDesc(@Param("limit") int limit);
    
    @Query("SELECT m.id, m.nom, m.prenom, COUNT(r) as rdvCount FROM RendezVous r JOIN r.medecin m GROUP BY m.id, m.nom, m.prenom ORDER BY rdvCount DESC LIMIT :limit")
    List<Object[]> findTopMedecinsByRendezVousCount(@Param("limit") int limit);
}