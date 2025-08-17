package ci.hardwork.gestionrdvservice.core.repository;

import ci.hardwork.gestionrdvservice.core.models.Medecin;
import ci.hardwork.gestionrdvservice.core.models.Patient;
import ci.hardwork.gestionrdvservice.core.models.RendezVous;
import ci.hardwork.gestionrdvservice.core.models.enums.RendezVousStatus;
import ci.hardwork.gestionrdvservice.core.models.enums.SpecialiteMedicale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests du Repository RendezVous")
class RendezVousRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private RendezVousRepository rendezVousRepository;
    
    private Patient patient1;
    private Patient patient2;
    private Medecin medecin1;
    private Medecin medecin2;
    private RendezVous rdv1;
    private RendezVous rdv2;
    private RendezVous rdv3;
    
    @BeforeEach
    void setUp() {
        // Créer des patients
        patient1 = createPatient("Dupont", "Jean", "1234567890123");
        patient2 = createPatient("Martin", "Marie", "9876543210987");
        
        // Créer des médecins
        medecin1 = createMedecin("Dr. Dubois", "Pierre", "11111111111", SpecialiteMedicale.CARDIOLOGIE);
        medecin2 = createMedecin("Dr. Moreau", "Sophie", "22222222222", SpecialiteMedicale.DERMATOLOGIE);
        
        // Persister les entités
        entityManager.persistAndFlush(patient1);
        entityManager.persistAndFlush(patient2);
        entityManager.persistAndFlush(medecin1);
        entityManager.persistAndFlush(medecin2);
        
        // Créer des rendez-vous
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime nextWeek = LocalDateTime.now().plusDays(7);
        
        rdv1 = createRendezVous(patient1, medecin1, tomorrow, tomorrow.plusMinutes(30), 
                               RendezVousStatus.CONFIRME, false);
        rdv2 = createRendezVous(patient1, medecin2, nextWeek, nextWeek.plusMinutes(45), 
                               RendezVousStatus.PLANIFIE, false);
        rdv3 = createRendezVous(patient2, medecin1, tomorrow.plusHours(2), tomorrow.plusHours(2).plusMinutes(30), 
                               RendezVousStatus.PLANIFIE, false);
        
        entityManager.persistAndFlush(rdv1);
        entityManager.persistAndFlush(rdv2);
        entityManager.persistAndFlush(rdv3);
    }
    
    @Test
    @DisplayName("Doit trouver les rendez-vous d'un patient triés par date décroissante")
    void shouldFindRendezVousByPatientOrderedByDateDesc() {
        // When
        List<RendezVous> rendezVous = rendezVousRepository.findByPatientIdOrderByDateHeureDebutDesc(patient1.getId());
        
        // Then
        assertThat(rendezVous).hasSize(2);
        assertThat(rendezVous.get(0).getDateHeureDebut()).isAfter(rendezVous.get(1).getDateHeureDebut());
    }
    
    @Test
    @DisplayName("Doit trouver les rendez-vous d'un médecin triés par date croissante")
    void shouldFindRendezVousByMedecinOrderedByDate() {
        // When
        List<RendezVous> rendezVous = rendezVousRepository.findByMedecinIdOrderByDateHeureDebut(medecin1.getId());
        
        // Then
        assertThat(rendezVous).hasSize(2);
        assertThat(rendezVous.get(0).getDateHeureDebut()).isBefore(rendezVous.get(1).getDateHeureDebut());
    }
    
    @Test
    @DisplayName("Doit trouver les rendez-vous d'un médecin entre deux dates")
    void shouldFindRendezVousByMedecinAndDateRange() {
        // Given
        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = LocalDateTime.now().plusDays(2);
        
        // When
        List<RendezVous> rendezVous = rendezVousRepository
                .findByMedecinIdAndDateHeureDebutBetweenOrderByDateHeureDebut(medecin1.getId(), debut, fin);
        
        // Then
        assertThat(rendezVous).hasSize(2);
    }
    
    @Test
    @DisplayName("Doit trouver les rendez-vous d'un patient entre deux dates")
    void shouldFindRendezVousByPatientAndDateRange() {
        // Given
        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = LocalDateTime.now().plusDays(2);
        
        // When
        List<RendezVous> rendezVous = rendezVousRepository
                .findByPatientIdAndDateHeureDebutBetweenOrderByDateHeureDebut(patient1.getId(), debut, fin);
        
        // Then
        assertThat(rendezVous).hasSize(1);
        assertThat(rendezVous.get(0).getStatut()).isEqualTo(RendezVousStatus.CONFIRME);
    }
    
    @Test
    @DisplayName("Doit trouver les rendez-vous par statut")
    void shouldFindRendezVousByStatus() {
        // When
        List<RendezVous> rendezVousPlanifies = rendezVousRepository.findByStatutOrderByDateHeureDebut(RendezVousStatus.PLANIFIE);
        List<RendezVous> rendezVousConfirmes = rendezVousRepository.findByStatutOrderByDateHeureDebut(RendezVousStatus.CONFIRME);
        
        // Then
        assertThat(rendezVousPlanifies).hasSize(2);
        assertThat(rendezVousConfirmes).hasSize(1);
    }
    
    @Test
    @DisplayName("Doit trouver les rendez-vous dans une plage de dates")
    void shouldFindRendezVousByDateRange() {
        // Given
        LocalDateTime debut = LocalDateTime.now();
        LocalDateTime fin = LocalDateTime.now().plusDays(2);
        
        // When
        List<RendezVous> rendezVous = rendezVousRepository.findByDateHeureDebutBetweenOrderByDateHeureDebut(debut, fin);
        
        // Then
        assertThat(rendezVous).hasSize(2);
    }
    
    @Test
    @DisplayName("Doit trouver les rendez-vous en conflit")
    void shouldFindConflictingRendezVous() {
        // Given
        LocalDateTime debut = rdv1.getDateHeureDebut().minusMinutes(15);
        LocalDateTime fin = rdv1.getDateHeureFin().plusMinutes(15);
        
        // When
        List<RendezVous> conflits = rendezVousRepository.findConflictingRendezVous(medecin1.getId(), debut, fin);
        
        // Then
        assertThat(conflits).hasSize(1);
        assertThat(conflits.get(0).getId()).isEqualTo(rdv1.getId());
    }
    
    @Test
    @DisplayName("Doit trouver les rendez-vous nécessitant un rappel")
    void shouldFindRendezVousRequiringReminder() {
        // Given
        LocalDateTime debut = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fin = debut.plusDays(1).minusNanos(1);
        
        // When
        List<RendezVous> rappels = rendezVousRepository.findRendezVousRequiringReminder(debut, fin);
        
        // Then
        // rdv1 et rdv3 sont tous les deux créés à tomorrow (jour+1), et rappelEnvoye=false
        // rdv2 est à nextWeek (jour+7), donc pas dans la période
        assertThat(rappels).hasSize(2);
    }
    
    @Test
    @DisplayName("Doit compter les rendez-vous par statut")
    void shouldCountRendezVousByStatus() {
        // When
        long countPlanifies = rendezVousRepository.countByStatut(RendezVousStatus.PLANIFIE);
        long countConfirmes = rendezVousRepository.countByStatut(RendezVousStatus.CONFIRME);
        
        // Then
        assertThat(countPlanifies).isEqualTo(2);
        assertThat(countConfirmes).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Doit sauvegarder un nouveau rendez-vous")
    void shouldSaveNewRendezVous() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(10);
        RendezVous newRdv = createRendezVous(patient2, medecin2, futureDate, futureDate.plusMinutes(30), 
                                           RendezVousStatus.PLANIFIE, false);
        
        // When
        RendezVous savedRdv = rendezVousRepository.save(newRdv);
        
        // Then
        assertThat(savedRdv.getId()).isNotNull();
        assertThat(savedRdv.getDateCreation()).isNotNull();
    }
    
    private static Long userIdCounter = 1000L;
    
    private Patient createPatient(String nom, String prenom, String nss) {
        Patient patient = new Patient();
        patient.setUserId(++userIdCounter); // Unique userId
        patient.setNom(nom);
        patient.setPrenom(prenom);
        patient.setNumeroSecuriteSociale(nss);
        patient.setEmail(nom.toLowerCase() + "." + prenom.toLowerCase() + "@email.com");
        patient.setDateNaissance(LocalDate.of(1990, 1, 1));
        patient.setTelephone("0123456789");
        patient.setAdresse("123 Rue Test");
        patient.setCodePostal("75001");
        patient.setVille("Paris");
        patient.setActif(true);
        patient.setDateCreation(LocalDateTime.now());
        return patient;
    }
    
    private static Long medecinUserIdCounter = 2000L;
    
    private Medecin createMedecin(String nom, String prenom, String rpps, SpecialiteMedicale specialite) {
        Medecin medecin = new Medecin();
        medecin.setUserId(++medecinUserIdCounter); // Unique userId
        medecin.setNom(nom);
        medecin.setPrenom(prenom);
        medecin.setNumeroRPPS(rpps);
        medecin.setSpecialite(specialite);
        medecin.setEmail(nom.toLowerCase().replace(" ", ".").replace(".", "") + "." + prenom.toLowerCase() + "@medical.com");
        medecin.setTelephone("0123456789");
        medecin.setAdresseCabinet("123 Avenue Medicale");
        medecin.setCodePostalCabinet("75001");
        medecin.setVilleCabinet("Paris");
        medecin.setDureeConsultationDefaut(30);
        medecin.setTarif(new BigDecimal("50.00"));
        medecin.setConventionne(true);
        medecin.setCarteVitaleAcceptee(true);
        medecin.setActif(true);
        medecin.setDateCreation(LocalDateTime.now());
        return medecin;
    }
    
    private RendezVous createRendezVous(Patient patient, Medecin medecin, LocalDateTime debut, 
                                      LocalDateTime fin, RendezVousStatus statut, boolean rappelEnvoye) {
        RendezVous rdv = new RendezVous();
        rdv.setPatient(patient);
        rdv.setMedecin(medecin);
        rdv.setDateHeureDebut(debut);
        rdv.setDateHeureFin(fin);
        rdv.setMotifConsultation("Consultation de contrôle");
        rdv.setStatut(statut);
        rdv.setTarif(new BigDecimal("50.00"));
        rdv.setRappelEnvoye(rappelEnvoye);
        rdv.setDateCreation(LocalDateTime.now());
        return rdv;
    }
}