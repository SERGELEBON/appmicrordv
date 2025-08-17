package ci.hardwork.gestionrdvservice.core.repository;

import ci.hardwork.gestionrdvservice.core.models.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests du Repository Patient")
class PatientRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private PatientRepository patientRepository;
    
    private Patient patient1;
    private Patient patient2;
    
    @BeforeEach
    void setUp() {
        patient1 = createPatient(
                "Dupont", "Jean", "1234567890123", "jean.dupont@email.com",
                LocalDate.of(1990, 1, 15), true
        );
        
        patient2 = createPatient(
                "Martin", "Marie", "9876543210987", "marie.martin@email.com",
                LocalDate.of(1985, 5, 20), false
        );
        
        entityManager.persistAndFlush(patient1);
        entityManager.persistAndFlush(patient2);
    }
    
    @Test
    @DisplayName("Doit trouver un patient par son numéro de sécurité sociale")
    void shouldFindPatientByNumeroSecuriteSociale() {
        // When
        Optional<Patient> foundPatient = patientRepository.findByNumeroSecuriteSociale("1234567890123");
        
        // Then
        assertThat(foundPatient).isPresent();
        assertThat(foundPatient.get().getNom()).isEqualTo("Dupont");
        assertThat(foundPatient.get().getPrenom()).isEqualTo("Jean");
    }
    
    @Test
    @DisplayName("Ne doit pas trouver un patient avec un NSS inexistant")
    void shouldNotFindPatientWithNonExistentNSS() {
        // When
        Optional<Patient> foundPatient = patientRepository.findByNumeroSecuriteSociale("0000000000000");
        
        // Then
        assertThat(foundPatient).isNotPresent();
    }
    
    @Test
    @DisplayName("Doit trouver un patient par son email")
    void shouldFindPatientByEmail() {
        // When
        Optional<Patient> foundPatient = patientRepository.findByEmail("jean.dupont@email.com");
        
        // Then
        assertThat(foundPatient).isPresent();
        assertThat(foundPatient.get().getNumeroSecuriteSociale()).isEqualTo("1234567890123");
    }
    
    @Test
    @DisplayName("Doit chercher des patients par nom (insensible à la casse)")
    void shouldFindPatientsByNomIgnoreCase() {
        // When
        List<Patient> patients = patientRepository.findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase("DUPONT", "");
        
        // Then
        assertThat(patients).hasSize(1);
        assertThat(patients.get(0).getNom()).isEqualTo("Dupont");
    }
    
    @Test
    @DisplayName("Doit chercher des patients par nom et prénom")
    void shouldFindPatientsByNomAndPrenom() {
        // When
        List<Patient> patients = patientRepository.findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase("martin", "marie");
        
        // Then
        assertThat(patients).hasSize(1);
        assertThat(patients.get(0).getEmail()).isEqualTo("marie.martin@email.com");
    }
    
    @Test
    @DisplayName("Doit vérifier l'existence d'un patient par NSS")
    void shouldCheckPatientExistenceByNSS() {
        // When & Then
        assertThat(patientRepository.existsByNumeroSecuriteSociale("1234567890123")).isTrue();
        assertThat(patientRepository.existsByNumeroSecuriteSociale("0000000000000")).isFalse();
    }
    
    @Test
    @DisplayName("Doit vérifier l'existence d'un patient par email")
    void shouldCheckPatientExistenceByEmail() {
        // When & Then
        assertThat(patientRepository.existsByEmail("jean.dupont@email.com")).isTrue();
        assertThat(patientRepository.existsByEmail("inexistant@email.com")).isFalse();
    }
    
    @Test
    @DisplayName("Doit compter les patients actifs")
    void shouldCountActivePatients() {
        // When
        long activeCount = patientRepository.countByActifTrue();
        
        // Then
        assertThat(activeCount).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Doit sauvegarder un nouveau patient")
    void shouldSaveNewPatient() {
        // Given
        Patient newPatient = createPatient(
                "Durand", "Pierre", "1111111111111", "pierre.durand@email.com",
                LocalDate.of(1975, 3, 10), true
        );
        
        // When
        Patient savedPatient = patientRepository.save(newPatient);
        
        // Then
        assertThat(savedPatient.getId()).isNotNull();
        assertThat(savedPatient.getDateCreation()).isNotNull();
        
        Optional<Patient> foundPatient = patientRepository.findById(savedPatient.getId());
        assertThat(foundPatient).isPresent();
        assertThat(foundPatient.get().getNom()).isEqualTo("Durand");
    }
    
    @Test
    @DisplayName("Doit mettre à jour un patient existant")
    void shouldUpdateExistingPatient() {
        // Given
        patient1.setTelephone("0123456789");
        patient1.setDateModification(LocalDateTime.now());
        
        // When
        Patient updatedPatient = patientRepository.save(patient1);
        
        // Then
        assertThat(updatedPatient.getTelephone()).isEqualTo("0123456789");
        assertThat(updatedPatient.getDateModification()).isNotNull();
    }
    
    private static Long userIdCounter = 1000L;
    
    private Patient createPatient(String nom, String prenom, String nss, String email, 
                                LocalDate dateNaissance, boolean actif) {
        Patient patient = new Patient();
        patient.setUserId(++userIdCounter); // Unique userId
        patient.setNom(nom);
        patient.setPrenom(prenom);
        patient.setNumeroSecuriteSociale(nss);
        patient.setEmail(email);
        patient.setDateNaissance(dateNaissance);
        patient.setTelephone("0123456789");
        patient.setAdresse("123 Rue Test");
        patient.setCodePostal("75001");
        patient.setVille("Paris");
        patient.setActif(actif);
        patient.setDateCreation(LocalDateTime.now());
        return patient;
    }
}