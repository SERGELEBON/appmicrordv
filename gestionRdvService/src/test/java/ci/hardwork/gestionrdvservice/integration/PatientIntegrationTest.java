package ci.hardwork.gestionrdvservice.integration;

import ci.hardwork.gestionrdvservice.GestionRdvServiceApplication;
import ci.hardwork.gestionrdvservice.config.TestContainersConfiguration;
import ci.hardwork.gestionrdvservice.core.dto.PatientCreateDTO;
import ci.hardwork.gestionrdvservice.core.dto.PatientUpdateDTO;
import ci.hardwork.gestionrdvservice.core.dto.PatientResponseDTO;
import ci.hardwork.gestionrdvservice.core.models.Patient;
import ci.hardwork.gestionrdvservice.core.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
    classes = {GestionRdvServiceApplication.class})
@AutoConfigureMockMvc
@Import({TestContainersConfiguration.class})
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'intégration Patient avec TestContainers")
class PatientIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private PatientRepository patientRepository;
    
    private PatientCreateDTO patientCreateDTO;
    
    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();
        
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
    }
    
    @Test
    @DisplayName("Doit créer un patient de bout en bout")
    void shouldCreatePatientEndToEnd() throws Exception {
        // When & Then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.prenom").value("Jean"))
                .andExpect(jsonPath("$.email").value("jean.dupont@email.com"))
                .andExpect(jsonPath("$.actif").value(true));
        
        // Vérifier en base de données
        assertThat(patientRepository.count()).isEqualTo(1);
        
        Patient savedPatient = patientRepository.findByEmail("jean.dupont@email.com").orElse(null);
        assertThat(savedPatient).isNotNull();
        assertThat(savedPatient.getNom()).isEqualTo("Dupont");
        assertThat(savedPatient.getNumeroSecuriteSociale()).isEqualTo("1234567890123");
    }
    
    @Test
    @DisplayName("Ne doit pas créer un patient avec un email déjà existant")
    void shouldNotCreatePatientWithDuplicateEmail() throws Exception {
        // Given - Créer un premier patient
        Patient existingPatient = new Patient();
        existingPatient.setUserId(2L);
        existingPatient.setNom("Martin");
        existingPatient.setPrenom("Marie");
        existingPatient.setNumeroSecuriteSociale("9876543210987");
        existingPatient.setEmail("jean.dupont@email.com"); // Même email
        existingPatient.setDateNaissance(LocalDate.of(1985, 5, 20));
        existingPatient.setTelephone("0987654321");
        existingPatient.setAdresse("456 Rue Autre");
        existingPatient.setCodePostal("69000");
        existingPatient.setVille("Lyon");
        existingPatient.setActif(true);
        existingPatient.setDateCreation(LocalDateTime.now());
        
        patientRepository.save(existingPatient);
        
        // When & Then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientCreateDTO)))
                .andExpect(status().isBadRequest());
        
        // Vérifier qu'il n'y a toujours qu'un seul patient
        assertThat(patientRepository.count()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Doit récupérer un patient par son ID")
    void shouldGetPatientById() throws Exception {
        // Given
        Patient savedPatient = createAndSavePatient();
        
        // When & Then
        mockMvc.perform(get("/patients/" + savedPatient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPatient.getId()))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.email").value("jean.dupont@email.com"));
    }
    
    @Test
    @DisplayName("Doit récupérer un patient par son NSS")
    void shouldGetPatientByNSS() throws Exception {
        // Given
        createAndSavePatient();
        
        // When & Then
        mockMvc.perform(get("/patients/nss/1234567890123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroSecuriteSociale").value("1234567890123"))
                .andExpect(jsonPath("$.nom").value("Dupont"));
    }
    
    @Test
    @DisplayName("Doit mettre à jour un patient existant")
    void shouldUpdateExistingPatient() throws Exception {
        // Given
        Patient savedPatient = createAndSavePatient();
        
        PatientUpdateDTO updateDTO = new PatientUpdateDTO();
        updateDTO.setNom("Dupont");
        updateDTO.setPrenom("Jean");
        updateDTO.setDateNaissance(LocalDate.of(1990, 1, 15));
        updateDTO.setEmail("jean.dupont.updated@email.com");
        updateDTO.setTelephone("0987654321");
        updateDTO.setAdresse("456 Rue Updated");
        updateDTO.setCodePostal("75002");
        updateDTO.setVille("Paris");
        updateDTO.setActif(true);
        
        // When & Then
        mockMvc.perform(put("/patients/" + savedPatient.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jean.dupont.updated@email.com"))
                .andExpect(jsonPath("$.telephone").value("0987654321"));
        
        // Vérifier en base
        Patient updatedPatient = patientRepository.findById(savedPatient.getId()).orElse(null);
        assertThat(updatedPatient).isNotNull();
        assertThat(updatedPatient.getEmail()).isEqualTo("jean.dupont.updated@email.com");
        assertThat(updatedPatient.getDateModification()).isNotNull();
    }
    
    @Test
    @DisplayName("Doit désactiver puis réactiver un patient")
    void shouldDeactivateAndReactivatePatient() throws Exception {
        // Given
        Patient savedPatient = createAndSavePatient();
        
        // When - Désactiver
        mockMvc.perform(patch("/patients/" + savedPatient.getId() + "/deactivate"))
                .andExpect(status().isOk());
        
        // Then - Vérifier la désactivation
        Patient deactivatedPatient = patientRepository.findById(savedPatient.getId()).orElse(null);
        assertThat(deactivatedPatient).isNotNull();
        assertThat(deactivatedPatient.getActif()).isFalse();
        
        // When - Réactiver
        mockMvc.perform(patch("/patients/" + savedPatient.getId() + "/activate"))
                .andExpect(status().isOk());
        
        // Then - Vérifier la réactivation
        Patient reactivatedPatient = patientRepository.findById(savedPatient.getId()).orElse(null);
        assertThat(reactivatedPatient).isNotNull();
        assertThat(reactivatedPatient.getActif()).isTrue();
    }
    
    @Test
    @DisplayName("Doit rechercher des patients par nom")
    void shouldSearchPatientsByName() throws Exception {
        // Given
        createAndSavePatient();
        
        Patient patient2 = new Patient();
        patient2.setUserId(3L);
        patient2.setNom("Durand");
        patient2.setPrenom("Pierre");
        patient2.setNumeroSecuriteSociale("1111111111111");
        patient2.setEmail("pierre.durand@email.com");
        patient2.setDateNaissance(LocalDate.of(1985, 3, 10));
        patient2.setTelephone("0111111111");
        patient2.setAdresse("789 Rue Autre");
        patient2.setCodePostal("69000");
        patient2.setVille("Lyon");
        patient2.setActif(true);
        patient2.setDateCreation(LocalDateTime.now());
        patientRepository.save(patient2);
        
        // When & Then
        mockMvc.perform(get("/patients/search")
                .param("nom", "Du")
                .param("prenom", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
    
    @Test
    @DisplayName("Doit supprimer un patient")
    void shouldDeletePatient() throws Exception {
        // Given
        Patient savedPatient = createAndSavePatient();
        assertThat(patientRepository.count()).isEqualTo(1);
        
        // When & Then
        mockMvc.perform(delete("/patients/" + savedPatient.getId()))
                .andExpect(status().isNoContent());
        
        // Vérifier la suppression en base
        assertThat(patientRepository.count()).isEqualTo(0);
        assertThat(patientRepository.findById(savedPatient.getId())).isNotPresent();
    }
    
    @Test
    @DisplayName("Doit retourner le nombre de patients actifs")
    void shouldReturnActivePatientCount() throws Exception {
        // Given
        createAndSavePatient();
        
        Patient inactivePatient = new Patient();
        inactivePatient.setUserId(4L);
        inactivePatient.setNom("Martin");
        inactivePatient.setPrenom("Marie");
        inactivePatient.setNumeroSecuriteSociale("9999999999999");
        inactivePatient.setEmail("marie.martin@email.com");
        inactivePatient.setDateNaissance(LocalDate.of(1980, 6, 25));
        inactivePatient.setTelephone("0999999999");
        inactivePatient.setAdresse("999 Rue Inactive");
        inactivePatient.setCodePostal("13000");
        inactivePatient.setVille("Marseille");
        inactivePatient.setActif(false); // Inactif
        inactivePatient.setDateCreation(LocalDateTime.now());
        patientRepository.save(inactivePatient);
        
        // When & Then
        mockMvc.perform(get("/patients/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("1")); // Seulement le patient actif
    }
    
    private Patient createAndSavePatient() {
        Patient patient = new Patient();
        patient.setUserId(1L);
        patient.setNom("Dupont");
        patient.setPrenom("Jean");
        patient.setNumeroSecuriteSociale("1234567890123");
        patient.setEmail("jean.dupont@email.com");
        patient.setDateNaissance(LocalDate.of(1990, 1, 15));
        patient.setTelephone("0123456789");
        patient.setAdresse("123 Rue Test");
        patient.setCodePostal("75001");
        patient.setVille("Paris");
        patient.setActif(true);
        patient.setDateCreation(LocalDateTime.now());
        
        return patientRepository.save(patient);
    }
}