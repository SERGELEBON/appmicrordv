package ci.hardwork.gestionrdvservice.core.service;

import ci.hardwork.gestionrdvservice.core.dto.RendezVousCreateDTO;
import ci.hardwork.gestionrdvservice.core.dto.RendezVousResponseDTO;
import ci.hardwork.gestionrdvservice.core.models.enums.RendezVousStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RendezVousService {
    
    RendezVousResponseDTO createRendezVous(RendezVousCreateDTO rendezVousCreateDTO);
    
    Optional<RendezVousResponseDTO> getRendezVousById(Long id);
    
    Page<RendezVousResponseDTO> getAllRendezVous(Pageable pageable);
    
    List<RendezVousResponseDTO> getRendezVousByPatient(Long patientId);
    
    List<RendezVousResponseDTO> getRendezVousByMedecin(Long medecinId);
    
    List<RendezVousResponseDTO> getRendezVousByMedecinAndDate(Long medecinId, LocalDate date);
    
    List<RendezVousResponseDTO> getRendezVousByPatientAndDateRange(Long patientId, LocalDateTime debut, LocalDateTime fin);
    
    List<RendezVousResponseDTO> getRendezVousByStatus(RendezVousStatus status);
    
    List<RendezVousResponseDTO> getRendezVousRequiringReminder();
    
    RendezVousResponseDTO updateRendezVous(Long id, RendezVousCreateDTO rendezVousUpdateDTO);
    
    RendezVousResponseDTO updateStatut(Long id, RendezVousStatus nouveauStatut);
    
    void cancelRendezVous(Long id, String motifAnnulation);
    
    void confirmRendezVous(Long id);
    
    void deleteRendezVous(Long id);
    
    boolean isCreneauDisponible(Long medecinId, LocalDateTime debut, LocalDateTime fin, Long excludeRendezVousId);
    
    List<LocalDateTime> getCreneauxLibres(Long medecinId, LocalDate date, int dureeEnMinutes);
    
    void envoyerRappel(Long rendezVousId);
    
    void marquerRappelEnvoye(Long rendezVousId);
    
    long countRendezVousByStatus(RendezVousStatus status);
    
    List<RendezVousResponseDTO> getRendezVousDuJour(LocalDate date);
    
    // Méthodes pour statistiques admin
    long countAllRendezVous();
    long countRendezVousByStatus(String status);
    int countRendezVousByDate(LocalDate date);
    int countRendezVousByDateRange(LocalDateTime debut, LocalDateTime fin);
    int countRendezVousCreatedAfter(LocalDateTime since);
    List<RendezVousResponseDTO> getRecentRendezVous(int limit);
    List<Object[]> getTopMedecinsByRendezVous(int limit);
}