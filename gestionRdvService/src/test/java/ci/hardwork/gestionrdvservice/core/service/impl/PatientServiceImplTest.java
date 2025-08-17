package ci.hardwork.gestionrdvservice.core.service.impl;

import ci.hardwork.gestionrdvservice.core.dto.PatientCreateDTO;
import ci.hardwork.gestionrdvservice.core.dto.PatientResponseDTO;
import ci.hardwork.gestionrdvservice.core.dto.PatientUpdateDTO;
import ci.hardwork.gestionrdvservice.core.mapper.PatientMapper;
import ci.hardwork.gestionrdvservice.core.models.Patient;
import ci.hardwork.gestionrdvservice.core.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Patient")
class PatientServiceImplTest {
    
    @Mock
    private PatientRepository patientRepository;
    
    @Mock
    private PatientMapper patientMapper;
    
    @InjectMocks
    private PatientServiceImpl patientService;
    
    private PatientCreateDTO patientCreateDTO;
    private PatientUpdateDTO patientUpdateDTO;
    private Patient patient;
    private PatientResponseDTO patientResponseDTO;
    
    @BeforeEach
    void setUp() {
        patientCreateDTO = new PatientCreateDTO();
        patientCreateDTO.setNom("Dupont");
        patientCreateDTO.setPrenom("Jean");
        patientCreateDTO.setNumeroSecuriteSociale("1234567890123");
        patientCreateDTO.setEmail("jean.dupont@email.com");
        patientCreateDTO.setDateNaissance(LocalDate.of(1990, 1, 15));
        patientCreateDTO.setTelephone("0123456789");
        patientCreateDTO.setAdresse("123 Rue Test");
        patientCreateDTO.setCodePostal("75001");
        patientCreateDTO.setVille("Paris");
        
        patientUpdateDTO = new PatientUpdateDTO();
        patientUpdateDTO.setNom("Dupont");
        patientUpdateDTO.setPrenom("Jean");
        patientUpdateDTO.setDateNaissance(LocalDate.of(1990, 1, 15));
        patientUpdateDTO.setEmail("jean.dupont.updated@email.com");
        patientUpdateDTO.setTelephone("0987654321");
        patientUpdateDTO.setAdresse("456 Rue Updated");
        patientUpdateDTO.setCodePostal("75002");
        patientUpdateDTO.setVille("Paris");
        patientUpdateDTO.setActif(true);
        
        patient = new Patient();
        patient.setId(1L);
        patient.setNom("Dupont");
        patient.setPrenom("Jean");
        patient.setNumeroSecuriteSociale("1234567890123");
        patient.setEmail("jean.dupont@email.com");
        patient.setDateNaissance(LocalDate.of(1990, 1, 15));
        patient.setActif(true);
        patient.setDateCreation(LocalDateTime.now());
        
        patientResponseDTO = new PatientResponseDTO();
        patientResponseDTO.setId(1L);
        patientResponseDTO.setNom("Dupont");
        patientResponseDTO.setPrenom("Jean");
        patientResponseDTO.setNumeroSecuriteSociale("1234567890123");
        patientResponseDTO.setEmail("jean.dupont@email.com");
        patientResponseDTO.setActif(true);
    }
    
    @Test
    @DisplayName("Doit créer un patient avec succès")
    void shouldCreatePatientSuccessfully() {
        // Given
        when(patientRepository.existsByNumeroSecuriteSociale(anyString())).thenReturn(false);
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientMapper.toEntity(any(PatientCreateDTO.class))).thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(patientResponseDTO);
        
        // When
        PatientResponseDTO result = patientService.createPatient(patientCreateDTO);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNom()).isEqualTo("Dupont");
        
        verify(patientRepository).existsByNumeroSecuriteSociale("1234567890123");
        verify(patientRepository).existsByEmail("jean.dupont@email.com");
        verify(patientRepository).save(any(Patient.class));
    }
    
    @Test
    @DisplayName("Ne doit pas créer un patient avec un NSS existant")
    void shouldNotCreatePatientWithExistingNSS() {
        // Given
        when(patientRepository.existsByNumeroSecuriteSociale(anyString())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> patientService.createPatient(patientCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Un patient avec ce numéro de sécurité sociale existe déjà");
        
        verify(patientRepository, never()).save(any(Patient.class));
    }
    
    @Test
    @DisplayName("Ne doit pas créer un patient avec un email existant")
    void shouldNotCreatePatientWithExistingEmail() {
        // Given
        when(patientRepository.existsByNumeroSecuriteSociale(anyString())).thenReturn(false);
        when(patientRepository.existsByEmail(anyString())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> patientService.createPatient(patientCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Un patient avec cet email existe déjà");
        
        verify(patientRepository, never()).save(any(Patient.class));
    }
    
    @Test
    @DisplayName("Doit récupérer un patient par ID")
    void shouldGetPatientById() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(patientResponseDTO);
        
        // When
        Optional<PatientResponseDTO> result = patientService.getPatientById(1L);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        
        verify(patientRepository).findById(1L);
    }
    
    @Test
    @DisplayName("Doit retourner Optional.empty pour un patient inexistant")
    void shouldReturnEmptyForNonExistentPatient() {
        // Given
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        Optional<PatientResponseDTO> result = patientService.getPatientById(999L);
        
        // Then
        assertThat(result).isNotPresent();
    }
    
    @Test
    @DisplayName("Doit récupérer tous les patients avec pagination")
    void shouldGetAllPatientsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(List.of(patient));
        
        when(patientRepository.findAll(pageable)).thenReturn(patientPage);
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(patientResponseDTO);
        
        // When
        Page<PatientResponseDTO> result = patientService.getAllPatients(pageable);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Doit rechercher des patients par nom et prénom")
    void shouldSearchPatientsByName() {
        // Given
        when(patientRepository.findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase("Dupont", "Jean"))
                .thenReturn(List.of(patient));
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(patientResponseDTO);
        
        // When
        List<PatientResponseDTO> result = patientService.searchPatientsByName("Dupont", "Jean");
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Dupont");
    }
    
    @Test
    @DisplayName("Doit mettre à jour un patient existant")
    void shouldUpdateExistingPatient() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(patientMapper.toResponseDTO(any(Patient.class))).thenReturn(patientResponseDTO);
        
        // When
        PatientResponseDTO result = patientService.updatePatient(1L, patientUpdateDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(patientMapper).updateEntityFromDTO(eq(patientUpdateDTO), eq(patient));
        verify(patientRepository).save(patient);
    }
    
    @Test
    @DisplayName("Ne doit pas mettre à jour un patient inexistant")
    void shouldNotUpdateNonExistentPatient() {
        // Given
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> patientService.updatePatient(999L, patientUpdateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Patient non trouvé avec l'ID: 999");
    }
    
    @Test
    @DisplayName("Doit supprimer un patient existant")
    void shouldDeleteExistingPatient() {
        // Given
        when(patientRepository.existsById(1L)).thenReturn(true);
        
        // When
        patientService.deletePatient(1L);
        
        // Then
        verify(patientRepository).deleteById(1L);
    }
    
    @Test
    @DisplayName("Ne doit pas supprimer un patient inexistant")
    void shouldNotDeleteNonExistentPatient() {
        // Given
        when(patientRepository.existsById(999L)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> patientService.deletePatient(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Patient non trouvé avec l'ID: 999");
        
        verify(patientRepository, never()).deleteById(999L);
    }
    
    @Test
    @DisplayName("Doit désactiver un patient")
    void shouldDeactivatePatient() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        
        // When
        patientService.deactivatePatient(1L);
        
        // Then
        verify(patientRepository).findById(1L);
        verify(patientRepository).save(patient);
        assertThat(patient.getActif()).isFalse();
    }
    
    @Test
    @DisplayName("Doit activer un patient")
    void shouldActivatePatient() {
        // Given
        patient.setActif(false);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        
        // When
        patientService.activatePatient(1L);
        
        // Then
        verify(patientRepository).findById(1L);
        verify(patientRepository).save(patient);
        assertThat(patient.getActif()).isTrue();
    }
    
    @Test
    @DisplayName("Doit vérifier l'existence d'un patient par NSS")
    void shouldCheckPatientExistenceByNSS() {
        // Given
        when(patientRepository.existsByNumeroSecuriteSociale("1234567890123")).thenReturn(true);
        
        // When
        boolean exists = patientService.existsByNumeroSecuriteSociale("1234567890123");
        
        // Then
        assertThat(exists).isTrue();
        verify(patientRepository).existsByNumeroSecuriteSociale("1234567890123");
    }
    
    @Test
    @DisplayName("Doit compter les patients actifs")
    void shouldCountActivePatients() {
        // Given
        when(patientRepository.countByActifTrue()).thenReturn(5L);
        
        // When
        long count = patientService.countActivePatients();
        
        // Then
        assertThat(count).isEqualTo(5L);
        verify(patientRepository).countByActifTrue();
    }
}