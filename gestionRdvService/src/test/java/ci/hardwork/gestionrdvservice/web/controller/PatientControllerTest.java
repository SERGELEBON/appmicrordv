package ci.hardwork.gestionrdvservice.web.controller;

import ci.hardwork.gestionrdvservice.core.dto.PatientCreateDTO;
import ci.hardwork.gestionrdvservice.core.dto.PatientResponseDTO;
import ci.hardwork.gestionrdvservice.core.dto.PatientUpdateDTO;
import ci.hardwork.gestionrdvservice.core.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@ActiveProfiles("test")
@DisplayName("Tests d'intégration du Controller Patient")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PatientControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private PatientService patientService;
    
    private PatientCreateDTO patientCreateDTO;
    private PatientUpdateDTO patientUpdateDTO;
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
        
        patientResponseDTO = new PatientResponseDTO();
        patientResponseDTO.setId(1L);
        patientResponseDTO.setNom("Dupont");
        patientResponseDTO.setPrenom("Jean");
        patientResponseDTO.setNumeroSecuriteSociale("1234567890123");
        patientResponseDTO.setEmail("jean.dupont@email.com");
        patientResponseDTO.setDateNaissance(LocalDate.of(1990, 1, 15));
        patientResponseDTO.setTelephone("0123456789");
        patientResponseDTO.setAdresse("123 Rue Test");
        patientResponseDTO.setCodePostal("75001");
        patientResponseDTO.setVille("Paris");
        patientResponseDTO.setActif(true);
        patientResponseDTO.setDateCreation(LocalDateTime.now());
    }
    
    @Test
    @DisplayName("POST /patients - Doit créer un patient avec succès")
    void shouldCreatePatientSuccessfully() throws Exception {
        // Given
        when(patientService.createPatient(any(PatientCreateDTO.class))).thenReturn(patientResponseDTO);
        
        // When & Then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.prenom").value("Jean"))
                .andExpect(jsonPath("$.email").value("jean.dupont@email.com"));
        
        verify(patientService).createPatient(any(PatientCreateDTO.class));
    }
    
    @Test
    @DisplayName("POST /patients - Doit retourner 400 pour des données invalides")
    void shouldReturn400ForInvalidData() throws Exception {
        // Given
        patientCreateDTO.setEmail("email-invalide"); // Email invalide
        
        // When & Then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientCreateDTO)))
                .andExpect(status().isBadRequest());
        
        verify(patientService, never()).createPatient(any(PatientCreateDTO.class));
    }
    
    @Test
    @DisplayName("POST /patients - Doit retourner 400 quand le service lance une exception")
    void shouldReturn400WhenServiceThrowsException() throws Exception {
        // Given
        when(patientService.createPatient(any(PatientCreateDTO.class)))
                .thenThrow(new IllegalArgumentException("Patient déjà existant"));
        
        // When & Then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientCreateDTO)))
                .andExpect(status().isBadRequest());
        
        verify(patientService).createPatient(any(PatientCreateDTO.class));
    }
    
    @Test
    @DisplayName("GET /patients/{id} - Doit retourner un patient existant")
    void shouldReturnExistingPatient() throws Exception {
        // Given
        when(patientService.getPatientById(1L)).thenReturn(Optional.of(patientResponseDTO));
        
        // When & Then
        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.email").value("jean.dupont@email.com"));
        
        verify(patientService).getPatientById(1L);
    }
    
    @Test
    @DisplayName("GET /patients/{id} - Doit retourner 404 pour un patient inexistant")
    void shouldReturn404ForNonExistentPatient() throws Exception {
        // Given
        when(patientService.getPatientById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/patients/999"))
                .andExpect(status().isNotFound());
        
        verify(patientService).getPatientById(999L);
    }
    
    @Test
    @DisplayName("GET /patients - Doit retourner une liste paginée de patients")
    void shouldReturnPagedPatients() throws Exception {
        // Given
        Page<PatientResponseDTO> patientPage = new PageImpl<>(
                List.of(patientResponseDTO), PageRequest.of(0, 10), 1);
        when(patientService.getAllPatients(any())).thenReturn(patientPage);
        
        // When & Then
        mockMvc.perform(get("/patients")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1L));
        
        verify(patientService).getAllPatients(any());
    }
    
    @Test
    @DisplayName("GET /patients/nss/{nss} - Doit trouver un patient par NSS")
    void shouldFindPatientByNSS() throws Exception {
        // Given
        when(patientService.getPatientByNumeroSecuriteSociale("1234567890123"))
                .thenReturn(Optional.of(patientResponseDTO));
        
        // When & Then
        mockMvc.perform(get("/patients/nss/1234567890123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroSecuriteSociale").value("1234567890123"));
        
        verify(patientService).getPatientByNumeroSecuriteSociale("1234567890123");
    }
    
    @Test
    @DisplayName("GET /patients/email/{email} - Doit trouver un patient par email")
    void shouldFindPatientByEmail() throws Exception {
        // Given
        when(patientService.getPatientByEmail("jean.dupont@email.com"))
                .thenReturn(Optional.of(patientResponseDTO));
        
        // When & Then
        mockMvc.perform(get("/patients/email/jean.dupont@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jean.dupont@email.com"));
        
        verify(patientService).getPatientByEmail("jean.dupont@email.com");
    }
    
    @Test
    @DisplayName("GET /patients/search - Doit rechercher des patients par nom")
    void shouldSearchPatientsByName() throws Exception {
        // Given
        when(patientService.searchPatientsByName("Dupont", "Jean"))
                .thenReturn(List.of(patientResponseDTO));
        
        // When & Then
        mockMvc.perform(get("/patients/search")
                .param("nom", "Dupont")
                .param("prenom", "Jean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nom").value("Dupont"));
        
        verify(patientService).searchPatientsByName("Dupont", "Jean");
    }
    
    @Test
    @DisplayName("PUT /patients/{id} - Doit mettre à jour un patient")
    void shouldUpdatePatient() throws Exception {
        // Given
        when(patientService.updatePatient(eq(1L), any(PatientUpdateDTO.class)))
                .thenReturn(patientResponseDTO);
        
        // When & Then
        mockMvc.perform(put("/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        
        verify(patientService).updatePatient(eq(1L), any(PatientUpdateDTO.class));
    }
    
    @Test
    @DisplayName("DELETE /patients/{id} - Doit supprimer un patient")
    void shouldDeletePatient() throws Exception {
        // Given
        doNothing().when(patientService).deletePatient(1L);
        
        // When & Then
        mockMvc.perform(delete("/patients/1"))
                .andExpect(status().isNoContent());
        
        verify(patientService).deletePatient(1L);
    }
    
    @Test
    @DisplayName("DELETE /patients/{id} - Doit retourner 404 pour un patient inexistant")
    void shouldReturn404WhenDeletingNonExistentPatient() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Patient non trouvé"))
                .when(patientService).deletePatient(999L);
        
        // When & Then
        mockMvc.perform(delete("/patients/999"))
                .andExpect(status().isNotFound());
        
        verify(patientService).deletePatient(999L);
    }
    
    @Test
    @DisplayName("PATCH /patients/{id}/deactivate - Doit désactiver un patient")
    void shouldDeactivatePatient() throws Exception {
        // Given
        doNothing().when(patientService).deactivatePatient(1L);
        
        // When & Then
        mockMvc.perform(patch("/patients/1/deactivate"))
                .andExpect(status().isOk());
        
        verify(patientService).deactivatePatient(1L);
    }
    
    @Test
    @DisplayName("PATCH /patients/{id}/activate - Doit activer un patient")
    void shouldActivatePatient() throws Exception {
        // Given
        doNothing().when(patientService).activatePatient(1L);
        
        // When & Then
        mockMvc.perform(patch("/patients/1/activate"))
                .andExpect(status().isOk());
        
        verify(patientService).activatePatient(1L);
    }
    
    @Test
    @DisplayName("GET /patients/count - Doit retourner le nombre de patients actifs")
    void shouldReturnActivePatientCount() throws Exception {
        // Given
        when(patientService.countActivePatients()).thenReturn(5L);
        
        // When & Then
        mockMvc.perform(get("/patients/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
        
        verify(patientService).countActivePatients();
    }
    
    @Test
    @DisplayName("GET /patients/{id}/exists - Doit vérifier l'existence d'un patient")
    void shouldCheckPatientExistence() throws Exception {
        // Given
        when(patientService.getPatientById(1L)).thenReturn(Optional.of(patientResponseDTO));
        
        // When & Then
        mockMvc.perform(get("/patients/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        
        verify(patientService).getPatientById(1L);
    }
}