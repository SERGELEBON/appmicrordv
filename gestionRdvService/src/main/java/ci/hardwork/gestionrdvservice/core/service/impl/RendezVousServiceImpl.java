package ci.hardwork.gestionrdvservice.core.service.impl;

import ci.hardwork.gestionrdvservice.core.dto.RendezVousCreateDTO;
import ci.hardwork.gestionrdvservice.core.dto.RendezVousResponseDTO;
import ci.hardwork.gestionrdvservice.core.mapper.RendezVousMapper;
import ci.hardwork.gestionrdvservice.core.models.Medecin;
import ci.hardwork.gestionrdvservice.core.models.Patient;
import ci.hardwork.gestionrdvservice.core.models.RendezVous;
import ci.hardwork.gestionrdvservice.core.models.enums.RendezVousStatus;
import ci.hardwork.gestionrdvservice.core.repository.MedecinRepository;
import ci.hardwork.gestionrdvservice.core.repository.PatientRepository;
import ci.hardwork.gestionrdvservice.core.repository.RendezVousRepository;
import ci.hardwork.gestionrdvservice.core.service.RendezVousService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RendezVousServiceImpl implements RendezVousService {
    
    private final RendezVousRepository rendezVousRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousMapper rendezVousMapper;
    
    @Override
    public RendezVousResponseDTO createRendezVous(RendezVousCreateDTO rendezVousCreateDTO) {
        log.info("Création d'un nouveau rendez-vous pour patient ID: {}, médecin ID: {}", 
                rendezVousCreateDTO.getPatientId(), rendezVousCreateDTO.getMedecinId());
        
        Patient patient = patientRepository.findById(rendezVousCreateDTO.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé"));
        
        Medecin medecin = medecinRepository.findById(rendezVousCreateDTO.getMedecinId())
                .orElseThrow(() -> new IllegalArgumentException("Médecin non trouvé"));
        
        if (!medecin.getActif()) {
            throw new IllegalArgumentException("Le médecin n'est pas actif");
        }
        
        // Validation du créneau disponible
        if (!isCreneauDisponible(medecin.getId(), 
                                rendezVousCreateDTO.getDateHeureDebut(), 
                                rendezVousCreateDTO.getDateHeureFin(), 
                                null)) {
            throw new IllegalArgumentException("Ce créneau n'est pas disponible");
        }
        
        RendezVous rendezVous = rendezVousMapper.toEntity(rendezVousCreateDTO);
        rendezVous.setPatient(patient);
        rendezVous.setMedecin(medecin);
        rendezVous.setDateCreation(LocalDateTime.now());
        
        RendezVous savedRendezVous = rendezVousRepository.save(rendezVous);
        log.info("Rendez-vous créé avec succès, ID: {}", savedRendezVous.getId());
        
        return rendezVousMapper.toResponseDTO(savedRendezVous);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<RendezVousResponseDTO> getRendezVousById(Long id) {
        return rendezVousRepository.findById(id)
                .map(rendezVousMapper::toResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RendezVousResponseDTO> getAllRendezVous(Pageable pageable) {
        return rendezVousRepository.findAll(pageable)
                .map(rendezVousMapper::toResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getRendezVousByPatient(Long patientId) {
        return rendezVousRepository.findByPatientIdOrderByDateHeureDebutDesc(patientId)
                .stream()
                .map(rendezVousMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getRendezVousByMedecin(Long medecinId) {
        return rendezVousRepository.findByMedecinIdOrderByDateHeureDebut(medecinId)
                .stream()
                .map(rendezVousMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getRendezVousByMedecinAndDate(Long medecinId, LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.atTime(23, 59, 59);
        
        return rendezVousRepository.findByMedecinIdAndDateHeureDebutBetweenOrderByDateHeureDebut(
                medecinId, debut, fin)
                .stream()
                .map(rendezVousMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getRendezVousByPatientAndDateRange(Long patientId, 
                                                                         LocalDateTime debut, 
                                                                         LocalDateTime fin) {
        return rendezVousRepository.findByPatientIdAndDateHeureDebutBetweenOrderByDateHeureDebut(
                patientId, debut, fin)
                .stream()
                .map(rendezVousMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getRendezVousByStatus(RendezVousStatus status) {
        return rendezVousRepository.findByStatutOrderByDateHeureDebut(status)
                .stream()
                .map(rendezVousMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getRendezVousRequiringReminder() {
        LocalDateTime demain = LocalDateTime.now().plusDays(1);
        LocalDateTime aprèsDemain = demain.plusDays(1);
        
        return rendezVousRepository.findRendezVousRequiringReminder(demain, aprèsDemain)
                .stream()
                .map(rendezVousMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    public RendezVousResponseDTO updateRendezVous(Long id, RendezVousCreateDTO rendezVousUpdateDTO) {
        log.info("Mise à jour du rendez-vous ID: {}", id);
        
        RendezVous existingRendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé"));
        
        // Validation du nouveau créneau si les dates changent
        if (!existingRendezVous.getDateHeureDebut().equals(rendezVousUpdateDTO.getDateHeureDebut()) ||
            !existingRendezVous.getDateHeureFin().equals(rendezVousUpdateDTO.getDateHeureFin())) {
            
            if (!isCreneauDisponible(rendezVousUpdateDTO.getMedecinId(),
                                   rendezVousUpdateDTO.getDateHeureDebut(),
                                   rendezVousUpdateDTO.getDateHeureFin(),
                                   id)) {
                throw new IllegalArgumentException("Ce nouveau créneau n'est pas disponible");
            }
        }
        
        rendezVousMapper.updateEntityFromDTO(rendezVousUpdateDTO, existingRendezVous);
        existingRendezVous.setDateModification(LocalDateTime.now());
        
        RendezVous savedRendezVous = rendezVousRepository.save(existingRendezVous);
        return rendezVousMapper.toResponseDTO(savedRendezVous);
    }
    
    @Override
    public RendezVousResponseDTO updateStatut(Long id, RendezVousStatus nouveauStatut) {
        log.info("Mise à jour du statut du rendez-vous ID: {} vers {}", id, nouveauStatut);
        
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé"));
        
        rendezVous.setStatut(nouveauStatut);
        rendezVous.setDateModification(LocalDateTime.now());
        
        RendezVous savedRendezVous = rendezVousRepository.save(rendezVous);
        return rendezVousMapper.toResponseDTO(savedRendezVous);
    }
    
    @Override
    public void cancelRendezVous(Long id, String motifAnnulation) {
        log.info("Annulation du rendez-vous ID: {}", id);
        
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé"));
        
        rendezVous.setStatut(RendezVousStatus.ANNULE);
        rendezVous.setNotes(rendezVous.getNotes() + "\nMotif d'annulation: " + motifAnnulation);
        rendezVous.setDateModification(LocalDateTime.now());
        
        rendezVousRepository.save(rendezVous);
        log.info("Rendez-vous annulé avec succès, ID: {}", id);
    }
    
    @Override
    public void confirmRendezVous(Long id) {
        updateStatut(id, RendezVousStatus.CONFIRME);
    }
    
    @Override
    public void deleteRendezVous(Long id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new IllegalArgumentException("Rendez-vous non trouvé");
        }
        rendezVousRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isCreneauDisponible(Long medecinId, LocalDateTime debut, LocalDateTime fin, Long excludeRendezVousId) {
        List<RendezVous> conflits = rendezVousRepository.findConflictingRendezVous(medecinId, debut, fin);
        
        if (excludeRendezVousId != null) {
            conflits = conflits.stream()
                    .filter(rdv -> !rdv.getId().equals(excludeRendezVousId))
                    .toList();
        }
        
        return conflits.isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LocalDateTime> getCreneauxLibres(Long medecinId, LocalDate date, int dureeEnMinutes) {
        // Implémentation basique - à enrichir avec les créneaux de disponibilité du médecin
        // Pour le moment, retourne des créneaux simples de 9h à 18h
        List<LocalDateTime> creneauxLibres = List.of();
        // TODO: Implémenter la logique complète avec les créneaux de disponibilité
        return creneauxLibres;
    }
    
    @Override
    public void envoyerRappel(Long rendezVousId) {
        log.info("Envoi de rappel pour le rendez-vous ID: {}", rendezVousId);
        // TODO: Intégrer avec un service d'email/SMS
        marquerRappelEnvoye(rendezVousId);
    }
    
    @Override
    public void marquerRappelEnvoye(Long rendezVousId) {
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé"));
        
        rendezVous.setRappelEnvoye(true);
        rendezVous.setDateRappel(LocalDateTime.now());
        rendezVousRepository.save(rendezVous);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countRendezVousByStatus(RendezVousStatus status) {
        return rendezVousRepository.countByStatut(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getRendezVousDuJour(LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.atTime(23, 59, 59);
        
        return rendezVousRepository.findByDateHeureDebutBetweenOrderByDateHeureDebut(debut, fin)
                .stream()
                .map(rendezVousMapper::toResponseDTO)
                .toList();
    }
    
    // Méthodes pour statistiques admin
    @Override
    @Transactional(readOnly = true)
    public long countAllRendezVous() {
        return rendezVousRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countRendezVousByStatus(String status) {
        try {
            RendezVousStatus enumStatus = RendezVousStatus.valueOf(status.toUpperCase());
            return rendezVousRepository.countByStatut(enumStatus);
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public int countRendezVousByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return rendezVousRepository.countByDateHeureDebutBetween(startOfDay, endOfDay);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int countRendezVousByDateRange(LocalDateTime debut, LocalDateTime fin) {
        return rendezVousRepository.countByDateHeureDebutBetween(debut, fin);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int countRendezVousCreatedAfter(LocalDateTime since) {
        return rendezVousRepository.countByDateCreationAfter(since);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RendezVousResponseDTO> getRecentRendezVous(int limit) {
        return rendezVousRepository.findTopByOrderByDateCreationDesc(limit)
                .stream()
                .map(rendezVousMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTopMedecinsByRendezVous(int limit) {
        return rendezVousRepository.findTopMedecinsByRendezVousCount(limit);
    }
}