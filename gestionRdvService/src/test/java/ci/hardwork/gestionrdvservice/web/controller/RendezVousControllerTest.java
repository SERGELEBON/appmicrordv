package ci.hardwork.gestionrdvservice.web.controller;

import ci.hardwork.gestionrdvservice.core.dto.RendezVousCreateDTO;
import ci.hardwork.gestionrdvservice.core.dto.RendezVousResponseDTO;
import ci.hardwork.gestionrdvservice.core.models.enums.RendezVousStatus;
import ci.hardwork.gestionrdvservice.core.service.RendezVousService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RendezVousController.class)
@ActiveProfiles("test")
@DisplayName("Tests d'intégration du Controller RendezVous")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RendezVousControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private RendezVousService rendezVousService;
    
    private RendezVousCreateDTO rendezVousCreateDTO;
    private RendezVousResponseDTO rendezVousResponseDTO;
    
    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        
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
        rendezVousResponseDTO.setMotifConsultation("Consultation de contrôle");
        rendezVousResponseDTO.setStatut(RendezVousStatus.PLANIFIE);
        rendezVousResponseDTO.setTarif(new BigDecimal("50.00"));
        rendezVousResponseDTO.setDateCreation(LocalDateTime.now());
    }
    
    @Test
    @DisplayName("POST /rdv - Doit créer un rendez-vous avec succès")
    void shouldCreateRendezVousSuccessfully() throws Exception {
        // Given
        when(rendezVousService.createRendezVous(any(RendezVousCreateDTO.class)))
                .thenReturn(rendezVousResponseDTO);
        
        // When & Then
        mockMvc.perform(post("/rdv")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rendezVousCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.patientId").value(1L))
                .andExpect(jsonPath("$.medecinId").value(1L))
                .andExpect(jsonPath("$.motifConsultation").value("Consultation de contrôle"))
                .andExpect(jsonPath("$.statut").value("PLANIFIE"));
        
        verify(rendezVousService).createRendezVous(any(RendezVousCreateDTO.class));
    }
    
    @Test
    @DisplayName("POST /rdv - Doit retourner 400 pour des données invalides")
    void shouldReturn400ForInvalidData() throws Exception {
        // Given
        rendezVousCreateDTO.setMotifConsultation(""); // Motif vide (invalide)
        
        // When & Then
        mockMvc.perform(post("/rdv")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rendezVousCreateDTO)))
                .andExpect(status().isBadRequest());
        
        verify(rendezVousService, never()).createRendezVous(any(RendezVousCreateDTO.class));
    }
    
    @Test
    @DisplayName("POST /rdv - Doit retourner 400 quand le créneau n'est pas disponible")
    void shouldReturn400WhenSlotNotAvailable() throws Exception {
        // Given
        when(rendezVousService.createRendezVous(any(RendezVousCreateDTO.class)))
                .thenThrow(new IllegalArgumentException("Ce créneau n'est pas disponible"));
        
        // When & Then
        mockMvc.perform(post("/rdv")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rendezVousCreateDTO)))
                .andExpect(status().isBadRequest());
        
        verify(rendezVousService).createRendezVous(any(RendezVousCreateDTO.class));
    }
    
    @Test
    @DisplayName("GET /rdv/{id} - Doit retourner un rendez-vous existant")
    void shouldReturnExistingRendezVous() throws Exception {
        // Given
        when(rendezVousService.getRendezVousById(1L)).thenReturn(Optional.of(rendezVousResponseDTO));
        
        // When & Then
        mockMvc.perform(get("/rdv/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.patientNom").value("Dupont"))
                .andExpect(jsonPath("$.medecinNom").value("Dr. Martin"));
        
        verify(rendezVousService).getRendezVousById(1L);
    }
    
    @Test
    @DisplayName("GET /rdv/{id} - Doit retourner 404 pour un rendez-vous inexistant")
    void shouldReturn404ForNonExistentRendezVous() throws Exception {
        // Given
        when(rendezVousService.getRendezVousById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/rdv/999"))
                .andExpect(status().isNotFound());
        
        verify(rendezVousService).getRendezVousById(999L);
    }
    
    @Test
    @DisplayName("GET /rdv - Doit retourner une liste paginée de rendez-vous")
    void shouldReturnPagedRendezVous() throws Exception {
        // Given
        Page<RendezVousResponseDTO> rdvPage = new PageImpl<>(
                List.of(rendezVousResponseDTO), PageRequest.of(0, 10), 1);
        when(rendezVousService.getAllRendezVous(any())).thenReturn(rdvPage);
        
        // When & Then
        mockMvc.perform(get("/rdv")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1L));
        
        verify(rendezVousService).getAllRendezVous(any());
    }
    
    @Test
    @DisplayName("GET /rdv/patient/{patientId} - Doit retourner les rendez-vous d'un patient")
    void shouldReturnPatientRendezVous() throws Exception {
        // Given
        when(rendezVousService.getRendezVousByPatient(1L))
                .thenReturn(List.of(rendezVousResponseDTO));
        
        // When & Then
        mockMvc.perform(get("/rdv/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].patientId").value(1L));
        
        verify(rendezVousService).getRendezVousByPatient(1L);
    }
    
    @Test
    @DisplayName("GET /rdv/medecin/{medecinId} - Doit retourner les rendez-vous d'un médecin")
    void shouldReturnMedecinRendezVous() throws Exception {
        // Given
        when(rendezVousService.getRendezVousByMedecin(1L))
                .thenReturn(List.of(rendezVousResponseDTO));
        
        // When & Then
        mockMvc.perform(get("/rdv/medecin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].medecinId").value(1L));
        
        verify(rendezVousService).getRendezVousByMedecin(1L);
    }
    
    @Test
    @DisplayName("GET /rdv/medecin/{medecinId}/date/{date} - Doit retourner les rendez-vous d'un médecin pour une date")
    void shouldReturnMedecinRendezVousByDate() throws Exception {
        // Given
        LocalDate date = LocalDate.now().plusDays(1);
        when(rendezVousService.getRendezVousByMedecinAndDate(1L, date))
                .thenReturn(List.of(rendezVousResponseDTO));
        
        // When & Then
        mockMvc.perform(get("/rdv/medecin/1/date/" + date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
        
        verify(rendezVousService).getRendezVousByMedecinAndDate(1L, date);
    }
    
    @Test
    @DisplayName("GET /rdv/statut/{status} - Doit retourner les rendez-vous par statut")
    void shouldReturnRendezVousByStatus() throws Exception {
        // Given
        when(rendezVousService.getRendezVousByStatus(RendezVousStatus.PLANIFIE))
                .thenReturn(List.of(rendezVousResponseDTO));
        
        // When & Then
        mockMvc.perform(get("/rdv/statut/PLANIFIE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].statut").value("PLANIFIE"));
        
        verify(rendezVousService).getRendezVousByStatus(RendezVousStatus.PLANIFIE);
    }
    
    @Test
    @DisplayName("GET /rdv/aujourdhui - Doit retourner les rendez-vous du jour")
    void shouldReturnTodayRendezVous() throws Exception {
        // Given
        when(rendezVousService.getRendezVousDuJour(any(LocalDate.class)))
                .thenReturn(List.of(rendezVousResponseDTO));
        
        // When & Then
        mockMvc.perform(get("/rdv/aujourdhui"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
        
        verify(rendezVousService).getRendezVousDuJour(any(LocalDate.class));
    }
    
    @Test
    @DisplayName("PUT /rdv/{id} - Doit mettre à jour un rendez-vous")
    void shouldUpdateRendezVous() throws Exception {
        // Given
        when(rendezVousService.updateRendezVous(eq(1L), any(RendezVousCreateDTO.class)))
                .thenReturn(rendezVousResponseDTO);
        
        // When & Then
        mockMvc.perform(put("/rdv/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rendezVousCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        
        verify(rendezVousService).updateRendezVous(eq(1L), any(RendezVousCreateDTO.class));
    }
    
    @Test
    @DisplayName("PATCH /rdv/{id}/statut - Doit mettre à jour le statut d'un rendez-vous")
    void shouldUpdateRendezVousStatus() throws Exception {
        // Given
        rendezVousResponseDTO.setStatut(RendezVousStatus.CONFIRME);
        when(rendezVousService.updateStatut(1L, RendezVousStatus.CONFIRME))
                .thenReturn(rendezVousResponseDTO);
        
        // When & Then
        mockMvc.perform(patch("/rdv/1/statut")
                .param("nouveauStatut", "CONFIRME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("CONFIRME"));
        
        verify(rendezVousService).updateStatut(1L, RendezVousStatus.CONFIRME);
    }
    
    @Test
    @DisplayName("PATCH /rdv/{id}/confirmer - Doit confirmer un rendez-vous")
    void shouldConfirmRendezVous() throws Exception {
        // Given
        doNothing().when(rendezVousService).confirmRendezVous(1L);
        
        // When & Then
        mockMvc.perform(patch("/rdv/1/confirmer"))
                .andExpect(status().isOk());
        
        verify(rendezVousService).confirmRendezVous(1L);
    }
    
    @Test
    @DisplayName("PATCH /rdv/{id}/annuler - Doit annuler un rendez-vous")
    void shouldCancelRendezVous() throws Exception {
        // Given
        doNothing().when(rendezVousService).cancelRendezVous(1L, "Patient indisponible");
        
        // When & Then
        mockMvc.perform(patch("/rdv/1/annuler")
                .param("motifAnnulation", "Patient indisponible"))
                .andExpect(status().isOk());
        
        verify(rendezVousService).cancelRendezVous(1L, "Patient indisponible");
    }
    
    @Test
    @DisplayName("DELETE /rdv/{id} - Doit supprimer un rendez-vous")
    void shouldDeleteRendezVous() throws Exception {
        // Given
        doNothing().when(rendezVousService).deleteRendezVous(1L);
        
        // When & Then
        mockMvc.perform(delete("/rdv/1"))
                .andExpect(status().isNoContent());
        
        verify(rendezVousService).deleteRendezVous(1L);
    }
    
    @Test
    @DisplayName("GET /rdv/creneau-disponible - Doit vérifier la disponibilité d'un créneau")
    void shouldCheckSlotAvailability() throws Exception {
        // Given
        LocalDateTime debut = LocalDateTime.now().plusDays(1);
        LocalDateTime fin = debut.plusMinutes(30);
        when(rendezVousService.isCreneauDisponible(1L, debut, fin, null)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(get("/rdv/creneau-disponible")
                .param("medecinId", "1")
                .param("debut", debut.toString())
                .param("fin", fin.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        
        verify(rendezVousService).isCreneauDisponible(1L, debut, fin, null);
    }
    
    @Test
    @DisplayName("POST /rdv/{id}/rappel - Doit envoyer un rappel")
    void shouldSendReminder() throws Exception {
        // Given
        doNothing().when(rendezVousService).envoyerRappel(1L);
        
        // When & Then
        mockMvc.perform(post("/rdv/1/rappel"))
                .andExpect(status().isOk());
        
        verify(rendezVousService).envoyerRappel(1L);
    }
    
    @Test
    @DisplayName("GET /rdv/statistiques/{status} - Doit retourner le nombre de rendez-vous par statut")
    void shouldReturnRendezVousCountByStatus() throws Exception {
        // Given
        when(rendezVousService.countRendezVousByStatus(RendezVousStatus.PLANIFIE)).thenReturn(5L);
        
        // When & Then
        mockMvc.perform(get("/rdv/statistiques/PLANIFIE"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
        
        verify(rendezVousService).countRendezVousByStatus(RendezVousStatus.PLANIFIE);
    }
}