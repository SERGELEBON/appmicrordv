package ci.hardwork.gestionrdvservice.core.service.impl;

import ci.hardwork.gestionrdvservice.core.dto.*;
import ci.hardwork.gestionrdvservice.core.mapper.PatientMapper;
import ci.hardwork.gestionrdvservice.core.models.Patient;
import ci.hardwork.gestionrdvservice.core.repository.PatientRepository;
import ci.hardwork.gestionrdvservice.core.service.PatientService;
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
public class PatientServiceImpl implements PatientService {
    
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    
    @Override
    public PatientResponseDTO createPatient(PatientCreateDTO patientCreateDTO) {
        log.info("Création d'un nouveau patient: {}", patientCreateDTO.getEmail());
        
        if (existsByNumeroSecuriteSociale(patientCreateDTO.getNumeroSecuriteSociale())) {
            throw new IllegalArgumentException("Un patient avec ce numéro de sécurité sociale existe déjà");
        }
        
        if (existsByEmail(patientCreateDTO.getEmail())) {
            throw new IllegalArgumentException("Un patient avec cet email existe déjà");
        }
        
        Patient patient = patientMapper.toEntity(patientCreateDTO);
        patient.setUserId(System.currentTimeMillis()); // Génération temporaire d'userId
        patient.setDateCreation(LocalDateTime.now());
        patient.setActif(true);
        
        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient créé avec succès, ID: {}", savedPatient.getId());
        
        return patientMapper.toResponseDTO(savedPatient);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PatientResponseDTO> getPatientById(Long id) {
        log.debug("Recherche du patient par ID: {}", id);
        return patientRepository.findById(id)
                .map(patientMapper::toResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PatientResponseDTO> getPatientByNumeroSecuriteSociale(String nss) {
        log.debug("Recherche du patient par NSS: {}", nss);
        return patientRepository.findByNumeroSecuriteSociale(nss)
                .map(patientMapper::toResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PatientResponseDTO> getPatientByEmail(String email) {
        log.debug("Recherche du patient par email: {}", email);
        return patientRepository.findByEmail(email)
                .map(patientMapper::toResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> getAllPatients(Pageable pageable) {
        log.debug("Récupération de tous les patients, page: {}", pageable.getPageNumber());
        return patientRepository.findAll(pageable)
                .map(patientMapper::toResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> searchPatientsByName(String nom, String prenom) {
        log.debug("Recherche de patients par nom: {} {}", nom, prenom);
        return patientRepository.findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(nom, prenom)
                .stream()
                .map(patientMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    public PatientResponseDTO updatePatient(Long id, PatientUpdateDTO patientUpdateDTO) {
        log.info("Mise à jour du patient ID: {}", id);
        
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé avec l'ID: " + id));
        
        // Vérifier l'unicité de l'email si modifié
        if (!existingPatient.getEmail().equals(patientUpdateDTO.getEmail()) &&
            existsByEmail(patientUpdateDTO.getEmail())) {
            throw new IllegalArgumentException("Un patient avec cet email existe déjà");
        }
        
        patientMapper.updateEntityFromDTO(patientUpdateDTO, existingPatient);
        existingPatient.setDateModification(LocalDateTime.now());
        
        Patient savedPatient = patientRepository.save(existingPatient);
        log.info("Patient mis à jour avec succès, ID: {}", savedPatient.getId());
        
        return patientMapper.toResponseDTO(savedPatient);
    }
    
    @Override
    public void deletePatient(Long id) {
        log.info("Suppression du patient ID: {}", id);
        
        if (!patientRepository.existsById(id)) {
            throw new IllegalArgumentException("Patient non trouvé avec l'ID: " + id);
        }
        
        patientRepository.deleteById(id);
        log.info("Patient supprimé avec succès, ID: {}", id);
    }
    
    @Override
    public void deactivatePatient(Long id) {
        log.info("Désactivation du patient ID: {}", id);
        updatePatientActiveStatus(id, false);
    }
    
    @Override
    public void activatePatient(Long id) {
        log.info("Activation du patient ID: {}", id);
        updatePatientActiveStatus(id, true);
    }
    
    private void updatePatientActiveStatus(Long id, boolean actif) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé avec l'ID: " + id));
        
        patient.setActif(actif);
        patient.setDateModification(LocalDateTime.now());
        patientRepository.save(patient);
        
        log.info("Statut du patient mis à jour, ID: {}, Actif: {}", id, actif);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNumeroSecuriteSociale(String nss) {
        return patientRepository.existsByNumeroSecuriteSociale(nss);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return patientRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countActivePatients() {
        return patientRepository.countByActifTrue();
    }
    
    // Méthodes pour statistiques admin
    @Override
    @Transactional(readOnly = true)
    public long countAllPatients() {
        return patientRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countPatientsActifs() {
        return patientRepository.countByActifTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    public int countPatientsByDateCreation(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return patientRepository.countByDateCreationBetween(startOfDay, endOfDay);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int countPatientsByDateRange(LocalDateTime debut, LocalDateTime fin) {
        return patientRepository.countByDateCreationBetween(debut, fin);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int countPatientsCreatedAfter(LocalDateTime since) {
        return patientRepository.countByDateCreationAfter(since);
    }
}