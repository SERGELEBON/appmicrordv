package ci.hardwork.gestionrdvservice.core.service;

import ci.hardwork.gestionrdvservice.core.dto.*;
import ci.hardwork.gestionrdvservice.core.models.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PatientService {
    
    PatientResponseDTO createPatient(PatientCreateDTO patientCreateDTO);
    
    Optional<PatientResponseDTO> getPatientById(Long id);
    
    Optional<PatientResponseDTO> getPatientByNumeroSecuriteSociale(String nss);
    
    Optional<PatientResponseDTO> getPatientByEmail(String email);
    
    Page<PatientResponseDTO> getAllPatients(Pageable pageable);
    
    List<PatientResponseDTO> searchPatientsByName(String nom, String prenom);
    
    PatientResponseDTO updatePatient(Long id, PatientUpdateDTO patientUpdateDTO);
    
    void deletePatient(Long id);
    
    void deactivatePatient(Long id);
    
    void activatePatient(Long id);
    
    boolean existsByNumeroSecuriteSociale(String nss);
    
    boolean existsByEmail(String email);
    
    long countActivePatients();
    
    // Méthodes pour statistiques admin
    long countAllPatients();
    long countPatientsActifs(); 
    int countPatientsByDateCreation(LocalDate date);
    int countPatientsByDateRange(LocalDateTime debut, LocalDateTime fin);
    int countPatientsCreatedAfter(LocalDateTime since);
}