package ci.hardwork.gestionrdvservice.core.service.impl;

import ci.hardwork.gestionrdvservice.core.dto.*;
import ci.hardwork.gestionrdvservice.core.mapper.CreneauMapper;
import ci.hardwork.gestionrdvservice.core.mapper.MedecinMapper;
import ci.hardwork.gestionrdvservice.core.models.CreneauDisponibilite;
import ci.hardwork.gestionrdvservice.core.models.Medecin;
import ci.hardwork.gestionrdvservice.core.models.enums.SpecialiteMedicale;
import ci.hardwork.gestionrdvservice.core.repository.MedecinRepository;
import ci.hardwork.gestionrdvservice.core.service.MedecinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MedecinServiceImpl implements MedecinService {
    
    private final MedecinRepository medecinRepository;
    private final MedecinMapper medecinMapper;
    private final CreneauMapper creneauMapper;
    
    @Override
    public MedecinResponseDTO createMedecin(MedecinCreateDTO medecinCreateDTO) {
        log.info("Création d'un nouveau médecin: {}", medecinCreateDTO.getEmail());
        
        if (existsByNumeroRPPS(medecinCreateDTO.getNumeroRPPS())) {
            throw new IllegalArgumentException("Un médecin avec ce numéro RPPS existe déjà");
        }
        
        Medecin medecin = medecinMapper.toEntity(medecinCreateDTO);
        medecin.setDateCreation(LocalDateTime.now());
        medecin.setActif(true);
        
        Medecin savedMedecin = medecinRepository.save(medecin);
        log.info("Médecin créé avec succès, ID: {}", savedMedecin.getId());
        
        return medecinMapper.toResponseDTO(savedMedecin);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MedecinResponseDTO> getMedecinById(Long id) {
        log.debug("Recherche du médecin par ID: {}", id);
        return medecinRepository.findById(id)
                .map(medecinMapper::toResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MedecinResponseDTO> getMedecinByNumeroRPPS(String numeroRPPS) {
        log.debug("Recherche du médecin par RPPS: {}", numeroRPPS);
        return medecinRepository.findByNumeroRPPS(numeroRPPS)
                .map(medecinMapper::toResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<MedecinResponseDTO> getAllMedecins(Pageable pageable) {
        log.debug("Récupération de tous les médecins, page: {}", pageable.getPageNumber());
        return medecinRepository.findAll(pageable)
                .map(medecinMapper::toResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedecinResponseDTO> getMedecinsBySpecialite(SpecialiteMedicale specialite) {
        log.debug("Recherche des médecins par spécialité: {}", specialite);
        return medecinRepository.findBySpecialite(specialite)
                .stream()
                .map(medecinMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedecinResponseDTO> getMedecinsByVille(String ville) {
        log.debug("Recherche des médecins par ville: {}", ville);
        return medecinRepository.findByVilleCabinetContainingIgnoreCase(ville)
                .stream()
                .map(medecinMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedecinResponseDTO> getActiveMedecins() {
        log.debug("Récupération des médecins actifs");
        return medecinRepository.findByActifTrue()
                .stream()
                .map(medecinMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    public MedecinResponseDTO updateMedecin(Long id, MedecinCreateDTO medecinUpdateDTO) {
        log.info("Mise à jour du médecin ID: {}", id);
        
        Medecin existingMedecin = medecinRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + id));
        
        // Vérifier l'unicité du RPPS si modifié
        if (!existingMedecin.getNumeroRPPS().equals(medecinUpdateDTO.getNumeroRPPS()) &&
            existsByNumeroRPPS(medecinUpdateDTO.getNumeroRPPS())) {
            throw new IllegalArgumentException("Un médecin avec ce numéro RPPS existe déjà");
        }
        
        medecinMapper.updateEntityFromDTO(medecinUpdateDTO, existingMedecin);
        existingMedecin.setDateModification(LocalDateTime.now());
        
        Medecin savedMedecin = medecinRepository.save(existingMedecin);
        log.info("Médecin mis à jour avec succès, ID: {}", savedMedecin.getId());
        
        return medecinMapper.toResponseDTO(savedMedecin);
    }
    
    @Override
    public void deleteMedecin(Long id) {
        log.info("Suppression du médecin ID: {}", id);
        
        if (!medecinRepository.existsById(id)) {
            throw new IllegalArgumentException("Médecin non trouvé avec l'ID: " + id);
        }
        
        medecinRepository.deleteById(id);
        log.info("Médecin supprimé avec succès, ID: {}", id);
    }
    
    @Override
    public void deactivateMedecin(Long id) {
        log.info("Désactivation du médecin ID: {}", id);
        updateMedecinActiveStatus(id, false);
    }
    
    @Override
    public void activateMedecin(Long id) {
        log.info("Activation du médecin ID: {}", id);
        updateMedecinActiveStatus(id, true);
    }
    
    private void updateMedecinActiveStatus(Long id, boolean actif) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + id));
        
        medecin.setActif(actif);
        medecin.setDateModification(LocalDateTime.now());
        medecinRepository.save(medecin);
        
        log.info("Statut du médecin mis à jour, ID: {}, Actif: {}", id, actif);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNumeroRPPS(String numeroRPPS) {
        return medecinRepository.existsByNumeroRPPS(numeroRPPS);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActiveMedecins() {
        return medecinRepository.countByActifTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CreneauDisponibiliteDTO> getCreneauxDisponibilite(Long medecinId) {
        log.debug("Récupération des créneaux de disponibilité pour le médecin ID: {}", medecinId);
        
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + medecinId));
        
        return medecin.getCreneauxDisponibilite()
                .stream()
                .filter(CreneauDisponibilite::getActif)
                .map(creneauMapper::toDTO)
                .toList();
    }
    
    @Override
    public CreneauDisponibiliteDTO addCreneauDisponibilite(Long medecinId, CreneauDisponibiliteDTO creneauDTO) {
        log.info("Ajout d'un créneau de disponibilité pour le médecin ID: {}", medecinId);
        
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé avec l'ID: " + medecinId));
        
        CreneauDisponibilite creneau = creneauMapper.toEntity(creneauDTO);
        creneau.setMedecin(medecin);
        creneau.setActif(true);
        
        medecin.getCreneauxDisponibilite().add(creneau);
        Medecin savedMedecin = medecinRepository.save(medecin);
        
        CreneauDisponibilite savedCreneau = savedMedecin.getCreneauxDisponibilite()
                .stream()
                .filter(c -> c.getJourSemaine().equals(creneau.getJourSemaine()) &&
                           c.getHeureDebut().equals(creneau.getHeureDebut()) &&
                           c.getHeureFin().equals(creneau.getHeureFin()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Erreur lors de la sauvegarde du créneau"));
        
        log.info("Créneau ajouté avec succès, ID: {}", savedCreneau.getId());
        return creneauMapper.toDTO(savedCreneau);
    }
    
    @Override
    public void removeCreneauDisponibilite(Long creneauId) {
        log.info("Suppression du créneau de disponibilité ID: {}", creneauId);
        
        // Implémentation simplifiée - dans un vrai projet, on aurait un CreneauRepository
        Medecin medecin = medecinRepository.findAll()
                .stream()
                .filter(m -> m.getCreneauxDisponibilite().stream()
                            .anyMatch(c -> c.getId().equals(creneauId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Créneau non trouvé avec l'ID: " + creneauId));
        
        medecin.getCreneauxDisponibilite().removeIf(c -> c.getId().equals(creneauId));
        medecinRepository.save(medecin);
        
        log.info("Créneau supprimé avec succès, ID: {}", creneauId);
    }
    
    // Méthodes pour statistiques admin
    @Override
    @Transactional(readOnly = true)
    public long countAllMedecins() {
        return medecinRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countMedecinsActifs() {
        return medecinRepository.countByActifTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public int countMedecinsByDateRange(LocalDateTime debut, LocalDateTime fin) {
        return medecinRepository.countByDateCreationBetween(debut, fin);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int countMedecinsCreatedAfter(LocalDateTime since) {
        return medecinRepository.countByDateCreationAfter(since);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getRepartitionParSpecialite() {
        List<Medecin> medecins = medecinRepository.findAll();
        return medecins.stream()
                .filter(Medecin::getActif)
                .collect(Collectors.groupingBy(
                    medecin -> medecin.getSpecialite().name(),
                    Collectors.counting()
                ));
    }
}