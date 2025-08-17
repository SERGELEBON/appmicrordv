package ci.hardwork.gestionrdvservice.core.service;

import ci.hardwork.gestionrdvservice.core.dto.*;
import ci.hardwork.gestionrdvservice.core.models.enums.SpecialiteMedicale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MedecinService {
    
    MedecinResponseDTO createMedecin(MedecinCreateDTO medecinCreateDTO);
    
    Optional<MedecinResponseDTO> getMedecinById(Long id);
    
    Optional<MedecinResponseDTO> getMedecinByNumeroRPPS(String numeroRPPS);
    
    Page<MedecinResponseDTO> getAllMedecins(Pageable pageable);
    
    List<MedecinResponseDTO> getMedecinsBySpecialite(SpecialiteMedicale specialite);
    
    List<MedecinResponseDTO> getMedecinsByVille(String ville);
    
    List<MedecinResponseDTO> getActiveMedecins();
    
    MedecinResponseDTO updateMedecin(Long id, MedecinCreateDTO medecinUpdateDTO);
    
    void deleteMedecin(Long id);
    
    void deactivateMedecin(Long id);
    
    void activateMedecin(Long id);
    
    boolean existsByNumeroRPPS(String numeroRPPS);
    
    long countActiveMedecins();
    
    List<CreneauDisponibiliteDTO> getCreneauxDisponibilite(Long medecinId);
    
    CreneauDisponibiliteDTO addCreneauDisponibilite(Long medecinId, CreneauDisponibiliteDTO creneauDTO);
    
    void removeCreneauDisponibilite(Long creneauId);
    
    // Méthodes pour statistiques admin
    long countAllMedecins();
    long countMedecinsActifs();
    int countMedecinsByDateRange(LocalDateTime debut, LocalDateTime fin);
    int countMedecinsCreatedAfter(LocalDateTime since);
    Map<String, Long> getRepartitionParSpecialite();
}