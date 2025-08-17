package ci.hardwork.gestionrdvservice.core.service.impl;

import ci.hardwork.gestionrdvservice.core.dto.RendezVousCreateDTO;
import ci.hardwork.gestionrdvservice.core.dto.RendezVousResponseDTO;
import ci.hardwork.gestionrdvservice.core.mapper.RendezVousMapper;
import ci.hardwork.gestionrdvservice.core.models.Medecin;
import ci.hardwork.gestionrdvservice.core.models.Patient;
import ci.hardwork.gestionrdvservice.core.models.RendezVous;
import ci.hardwork.gestionrdvservice.core.models.enums.RendezVousStatus;
import ci.hardwork.gestionrdvservice.core.models.enums.SpecialiteMedicale;
import ci.hardwork.gestionrdvservice.core.repository.MedecinRepository;
import ci.hardwork.gestionrdvservice.core.repository.PatientRepository;
import ci.hardwork.gestionrdvservice.core.repository.RendezVousRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service RendezVous")
class RendezVousServiceImplTest {
    
    @Mock
    private RendezVousRepository rendezVousRepository;
    
    @Mock
    private PatientRepository patientRepository;
    
    @Mock
    private MedecinRepository medecinRepository;
    
    @Mock
    private RendezVousMapper rendezVousMapper;
    
    @InjectMocks
    private RendezVousServiceImpl rendezVousService;
    
    private RendezVousCreateDTO rendezVousCreateDTO;
    private Patient patient;
    private Medecin medecin;
    private RendezVous rendezVous;
    private RendezVousResponseDTO rendezVousResponseDTO;
    
    @BeforeEach
    void setUp() {
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
        
        rendezVousCreateDTO = new RendezVousCreateDTO();
        rendezVousCreateDTO.setPatientId(1L);
        rendezVousCreateDTO.setMedecinId(1L);
        rendezVousCreateDTO.setDateHeureDebut(futureDateTime);
        rendezVousCreateDTO.setDateHeureFin(futureDateTime.plusMinutes(30));
        rendezVousCreateDTO.setMotifConsultation("Consultation de contrôle");
        rendezVousCreateDTO.setStatut(RendezVousStatus.PLANIFIE);
        rendezVousCreateDTO.setTarif(new BigDecimal("50.00"));
        rendezVousCreateDTO.setRappelEnvoye(false);
        
        patient = new Patient();
        patient.setId(1L);
        patient.setNom("Dupont");
        patient.setPrenom("Jean");
        patient.setActif(true);
        
        medecin = new Medecin();
        medecin.setId(1L);
        medecin.setNom("Dr. Martin");
        medecin.setPrenom("Sophie");
        medecin.setSpecialite(SpecialiteMedicale.CARDIOLOGIE);
        medecin.setActif(true);
        
        rendezVous = new RendezVous();
        rendezVous.setId(1L);
        rendezVous.setPatient(patient);
        rendezVous.setMedecin(medecin);
        rendezVous.setDateHeureDebut(futureDateTime);
        rendezVous.setDateHeureFin(futureDateTime.plusMinutes(30));
        rendezVous.setMotifConsultation("Consultation de contrôle");
        rendezVous.setStatut(RendezVousStatus.PLANIFIE);
        rendezVous.setTarif(new BigDecimal("50.00"));
        rendezVous.setDateCreation(LocalDateTime.now());
        
        rendezVousResponseDTO = new RendezVousResponseDTO();
        rendezVousResponseDTO.setId(1L);
        rendezVousResponseDTO.setPatientId(1L);
        rendezVousResponseDTO.setPatientNom("Dupont");
        rendezVousResponseDTO.setPatientPrenom("Jean");
        rendezVousResponseDTO.setMedecinId(1L);
        rendezVousResponseDTO.setMedecinNom("Dr. Martin");
        rendezVousResponseDTO.setMedecinPrenom("Sophie");
        rendezVousResponseDTO.setDateHeureDebut(futureDateTime);
        rendezVousResponseDTO.setDateHeureFin(futureDateTime.plusMinutes(30));
        rendezVousResponseDTO.setStatut(RendezVousStatus.PLANIFIE);
    }
    
    @Test
    @DisplayName("Doit créer un rendez-vous avec succès")
    void shouldCreateRendezVousSuccessfully() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medecinRepository.findById(1L)).thenReturn(Optional.of(medecin));
        when(rendezVousRepository.findConflictingRendezVous(anyLong(), any(), any())).thenReturn(List.of());
        when(rendezVousMapper.toEntity(any(RendezVousCreateDTO.class))).thenReturn(rendezVous);
        when(rendezVousRepository.save(any(RendezVous.class))).thenReturn(rendezVous);
        when(rendezVousMapper.toResponseDTO(any(RendezVous.class))).thenReturn(rendezVousResponseDTO);
        
        // When
        RendezVousResponseDTO result = rendezVousService.createRendezVous(rendezVousCreateDTO);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatut()).isEqualTo(RendezVousStatus.PLANIFIE);
        
        verify(patientRepository).findById(1L);
        verify(medecinRepository).findById(1L);
        verify(rendezVousRepository).save(any(RendezVous.class));
    }
    
    @Test
    @DisplayName("Ne doit pas créer un rendez-vous avec un patient inexistant")
    void shouldNotCreateRendezVousWithNonExistentPatient() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> rendezVousService.createRendezVous(rendezVousCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Patient non trouvé");
        
        verify(rendezVousRepository, never()).save(any(RendezVous.class));
    }
    
    @Test
    @DisplayName("Ne doit pas créer un rendez-vous avec un médecin inexistant")
    void shouldNotCreateRendezVousWithNonExistentMedecin() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medecinRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> rendezVousService.createRendezVous(rendezVousCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Médecin non trouvé");
        
        verify(rendezVousRepository, never()).save(any(RendezVous.class));
    }
    
    @Test
    @DisplayName("Ne doit pas créer un rendez-vous avec un médecin inactif")
    void shouldNotCreateRendezVousWithInactiveMedecin() {
        // Given
        medecin.setActif(false);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medecinRepository.findById(1L)).thenReturn(Optional.of(medecin));
        
        // When & Then
        assertThatThrownBy(() -> rendezVousService.createRendezVous(rendezVousCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le médecin n'est pas actif");
        
        verify(rendezVousRepository, never()).save(any(RendezVous.class));
    }
    
    @Test
    @DisplayName("Ne doit pas créer un rendez-vous si le créneau n'est pas disponible")
    void shouldNotCreateRendezVousWithUnavailableSlot() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medecinRepository.findById(1L)).thenReturn(Optional.of(medecin));
        when(rendezVousRepository.findConflictingRendezVous(anyLong(), any(), any()))
                .thenReturn(List.of(rendezVous));
        
        // When & Then
        assertThatThrownBy(() -> rendezVousService.createRendezVous(rendezVousCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ce créneau n'est pas disponible");
        
        verify(rendezVousRepository, never()).save(any(RendezVous.class));
    }
    
    @Test
    @DisplayName("Doit récupérer un rendez-vous par ID")
    void shouldGetRendezVousById() {
        // Given
        when(rendezVousRepository.findById(1L)).thenReturn(Optional.of(rendezVous));
        when(rendezVousMapper.toResponseDTO(any(RendezVous.class))).thenReturn(rendezVousResponseDTO);
        
        // When
        Optional<RendezVousResponseDTO> result = rendezVousService.getRendezVousById(1L);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        
        verify(rendezVousRepository).findById(1L);
    }
    
    @Test
    @DisplayName("Doit récupérer tous les rendez-vous avec pagination")
    void shouldGetAllRendezVousWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<RendezVous> rendezVousPage = new PageImpl<>(List.of(rendezVous));
        
        when(rendezVousRepository.findAll(pageable)).thenReturn(rendezVousPage);
        when(rendezVousMapper.toResponseDTO(any(RendezVous.class))).thenReturn(rendezVousResponseDTO);
        
        // When
        Page<RendezVousResponseDTO> result = rendezVousService.getAllRendezVous(pageable);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Doit récupérer les rendez-vous d'un patient")
    void shouldGetRendezVousByPatient() {
        // Given
        when(rendezVousRepository.findByPatientIdOrderByDateHeureDebutDesc(1L))
                .thenReturn(List.of(rendezVous));
        when(rendezVousMapper.toResponseDTO(any(RendezVous.class))).thenReturn(rendezVousResponseDTO);
        
        // When
        List<RendezVousResponseDTO> result = rendezVousService.getRendezVousByPatient(1L);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Doit récupérer les rendez-vous d'un médecin")
    void shouldGetRendezVousByMedecin() {
        // Given
        when(rendezVousRepository.findByMedecinIdOrderByDateHeureDebut(1L))
                .thenReturn(List.of(rendezVous));
        when(rendezVousMapper.toResponseDTO(any(RendezVous.class))).thenReturn(rendezVousResponseDTO);
        
        // When
        List<RendezVousResponseDTO> result = rendezVousService.getRendezVousByMedecin(1L);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMedecinId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Doit récupérer les rendez-vous par statut")
    void shouldGetRendezVousByStatus() {
        // Given
        when(rendezVousRepository.findByStatutOrderByDateHeureDebut(RendezVousStatus.PLANIFIE))
                .thenReturn(List.of(rendezVous));
        when(rendezVousMapper.toResponseDTO(any(RendezVous.class))).thenReturn(rendezVousResponseDTO);
        
        // When
        List<RendezVousResponseDTO> result = rendezVousService.getRendezVousByStatus(RendezVousStatus.PLANIFIE);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatut()).isEqualTo(RendezVousStatus.PLANIFIE);
    }
    
    @Test
    @DisplayName("Doit mettre à jour le statut d'un rendez-vous")
    void shouldUpdateRendezVousStatus() {
        // Given
        when(rendezVousRepository.findById(1L)).thenReturn(Optional.of(rendezVous));
        when(rendezVousRepository.save(any(RendezVous.class))).thenReturn(rendezVous);
        when(rendezVousMapper.toResponseDTO(any(RendezVous.class))).thenReturn(rendezVousResponseDTO);
        
        // When
        RendezVousResponseDTO result = rendezVousService.updateStatut(1L, RendezVousStatus.CONFIRME);
        
        // Then
        assertThat(result).isNotNull();
        verify(rendezVousRepository).save(rendezVous);
        assertThat(rendezVous.getStatut()).isEqualTo(RendezVousStatus.CONFIRME);
    }
    
    @Test
    @DisplayName("Doit annuler un rendez-vous")
    void shouldCancelRendezVous() {
        // Given
        when(rendezVousRepository.findById(1L)).thenReturn(Optional.of(rendezVous));
        when(rendezVousRepository.save(any(RendezVous.class))).thenReturn(rendezVous);
        
        // When
        rendezVousService.cancelRendezVous(1L, "Patient indisponible");
        
        // Then
        verify(rendezVousRepository).save(rendezVous);
        assertThat(rendezVous.getStatut()).isEqualTo(RendezVousStatus.ANNULE);
        assertThat(rendezVous.getNotes()).contains("Patient indisponible");
    }
    
    @Test
    @DisplayName("Doit confirmer un rendez-vous")
    void shouldConfirmRendezVous() {
        // Given
        when(rendezVousRepository.findById(1L)).thenReturn(Optional.of(rendezVous));
        when(rendezVousRepository.save(any(RendezVous.class))).thenReturn(rendezVous);
        when(rendezVousMapper.toResponseDTO(any(RendezVous.class))).thenReturn(rendezVousResponseDTO);
        
        // When
        rendezVousService.confirmRendezVous(1L);
        
        // Then
        verify(rendezVousRepository).save(rendezVous);
        assertThat(rendezVous.getStatut()).isEqualTo(RendezVousStatus.CONFIRME);
    }
    
    @Test
    @DisplayName("Doit vérifier si un créneau est disponible")
    void shouldCheckIfSlotIsAvailable() {
        // Given
        LocalDateTime debut = LocalDateTime.now().plusDays(1);
        LocalDateTime fin = debut.plusMinutes(30);
        when(rendezVousRepository.findConflictingRendezVous(1L, debut, fin)).thenReturn(List.of());
        
        // When
        boolean available = rendezVousService.isCreneauDisponible(1L, debut, fin, null);
        
        // Then
        assertThat(available).isTrue();
        verify(rendezVousRepository).findConflictingRendezVous(1L, debut, fin);
    }
    
    @Test
    @DisplayName("Doit détecter un conflit de créneau")
    void shouldDetectSlotConflict() {
        // Given
        LocalDateTime debut = LocalDateTime.now().plusDays(1);
        LocalDateTime fin = debut.plusMinutes(30);
        when(rendezVousRepository.findConflictingRendezVous(1L, debut, fin)).thenReturn(List.of(rendezVous));
        
        // When
        boolean available = rendezVousService.isCreneauDisponible(1L, debut, fin, null);
        
        // Then
        assertThat(available).isFalse();
    }
    
    @Test
    @DisplayName("Doit marquer un rappel comme envoyé")
    void shouldMarkReminderAsSent() {
        // Given
        when(rendezVousRepository.findById(1L)).thenReturn(Optional.of(rendezVous));
        when(rendezVousRepository.save(any(RendezVous.class))).thenReturn(rendezVous);
        
        // When
        rendezVousService.marquerRappelEnvoye(1L);
        
        // Then
        verify(rendezVousRepository).save(rendezVous);
        assertThat(rendezVous.getRappelEnvoye()).isTrue();
        assertThat(rendezVous.getDateRappel()).isNotNull();
    }
    
    @Test
    @DisplayName("Doit compter les rendez-vous par statut")
    void shouldCountRendezVousByStatus() {
        // Given
        when(rendezVousRepository.countByStatut(RendezVousStatus.PLANIFIE)).thenReturn(5L);
        
        // When
        long count = rendezVousService.countRendezVousByStatus(RendezVousStatus.PLANIFIE);
        
        // Then
        assertThat(count).isEqualTo(5L);
        verify(rendezVousRepository).countByStatut(RendezVousStatus.PLANIFIE);
    }
}