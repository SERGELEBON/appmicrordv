package ci.hardwork.gestionrdvservice.core.repository;

import ci.hardwork.gestionrdvservice.core.models.Medecin;
import ci.hardwork.gestionrdvservice.core.models.enums.SpecialiteMedicale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests du Repository Médecin")
class MedecinRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private MedecinRepository medecinRepository;
    
    private Medecin medecin1;
    private Medecin medecin2;
    private Medecin medecin3;
    
    @BeforeEach
    void setUp() {
        medecin1 = createMedecin(
                "Dr. Dubois", "Jean", "12345678901", SpecialiteMedicale.CARDIOLOGIE,
                "jean.dubois@medical.com", "Paris", true
        );
        
        medecin2 = createMedecin(
                "Dr. Moreau", "Marie", "98765432109", SpecialiteMedicale.DERMATOLOGIE,
                "marie.moreau@medical.com", "Lyon", true
        );
        
        medecin3 = createMedecin(
                "Dr. Bernard", "Pierre", "11111111111", SpecialiteMedicale.CARDIOLOGIE,
                "pierre.bernard@medical.com", "Paris", false
        );
        
        entityManager.persistAndFlush(medecin1);
        entityManager.persistAndFlush(medecin2);
        entityManager.persistAndFlush(medecin3);
    }
    
    @Test
    @DisplayName("Doit trouver un médecin par son numéro RPPS")
    void shouldFindMedecinByNumeroRPPS() {
        // When
        Optional<Medecin> foundMedecin = medecinRepository.findByNumeroRPPS("12345678901");
        
        // Then
        assertThat(foundMedecin).isPresent();
        assertThat(foundMedecin.get().getNom()).isEqualTo("Dr. Dubois");
        assertThat(foundMedecin.get().getSpecialite()).isEqualTo(SpecialiteMedicale.CARDIOLOGIE);
    }
    
    @Test
    @DisplayName("Doit trouver les médecins par spécialité")
    void shouldFindMedecinsBySpecialite() {
        // When
        List<Medecin> cardiologues = medecinRepository.findBySpecialite(SpecialiteMedicale.CARDIOLOGIE);
        
        // Then
        assertThat(cardiologues).hasSize(2);
        assertThat(cardiologues).extracting(Medecin::getNom)
                .containsExactlyInAnyOrder("Dr. Dubois", "Dr. Bernard");
    }
    
    @Test
    @DisplayName("Doit trouver les médecins par ville (insensible à la casse)")
    void shouldFindMedecinsByVille() {
        // When
        List<Medecin> medecinsLyon = medecinRepository.findByVilleCabinetContainingIgnoreCase("lyon");
        
        // Then
        assertThat(medecinsLyon).hasSize(1);
        assertThat(medecinsLyon.get(0).getNom()).isEqualTo("Dr. Moreau");
    }
    
    @Test
    @DisplayName("Doit trouver les médecins actifs")
    void shouldFindActiveMedecins() {
        // When
        List<Medecin> medecinsActifs = medecinRepository.findByActifTrue();
        
        // Then
        assertThat(medecinsActifs).hasSize(2);
        assertThat(medecinsActifs).extracting(Medecin::getNom)
                .containsExactlyInAnyOrder("Dr. Dubois", "Dr. Moreau");
    }
    
    @Test
    @DisplayName("Doit vérifier l'existence d'un médecin par RPPS")
    void shouldCheckMedecinExistenceByRPPS() {
        // When & Then
        assertThat(medecinRepository.existsByNumeroRPPS("12345678901")).isTrue();
        assertThat(medecinRepository.existsByNumeroRPPS("00000000000")).isFalse();
    }
    
    @Test
    @DisplayName("Doit compter les médecins actifs")
    void shouldCountActiveMedecins() {
        // When
        long activeCount = medecinRepository.countByActifTrue();
        
        // Then
        assertThat(activeCount).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Doit sauvegarder un nouveau médecin")
    void shouldSaveNewMedecin() {
        // Given
        Medecin newMedecin = createMedecin(
                "Dr. Leroy", "Sophie", "22222222222", SpecialiteMedicale.PEDIATRIE,
                "sophie.leroy@medical.com", "Marseille", true
        );
        
        // When
        Medecin savedMedecin = medecinRepository.save(newMedecin);
        
        // Then
        assertThat(savedMedecin.getId()).isNotNull();
        assertThat(savedMedecin.getDateCreation()).isNotNull();
        
        Optional<Medecin> foundMedecin = medecinRepository.findById(savedMedecin.getId());
        assertThat(foundMedecin).isPresent();
        assertThat(foundMedecin.get().getSpecialite()).isEqualTo(SpecialiteMedicale.PEDIATRIE);
    }
    
    @Test
    @DisplayName("Doit mettre à jour un médecin existant")
    void shouldUpdateExistingMedecin() {
        // Given
        medecin1.setTarif(new BigDecimal("75.00"));
        medecin1.setDateModification(LocalDateTime.now());
        
        // When
        Medecin updatedMedecin = medecinRepository.save(medecin1);
        
        // Then
        assertThat(updatedMedecin.getTarif()).isEqualByComparingTo("75.00");
        assertThat(updatedMedecin.getDateModification()).isNotNull();
    }
    
    @Test
    @DisplayName("Doit trouver les médecins par spécialité et ville")
    void shouldFindMedecinsBySpecialiteAndVille() {
        // When
        List<Medecin> cardiologuesParis = medecinRepository.findBySpecialite(SpecialiteMedicale.CARDIOLOGIE)
                .stream()
                .filter(m -> m.getVilleCabinet().equalsIgnoreCase("Paris"))
                .toList();
        
        // Then
        assertThat(cardiologuesParis).hasSize(2);
    }
    
    private static Long userIdCounter = 2000L;
    
    private Medecin createMedecin(String nom, String prenom, String rpps, 
                                SpecialiteMedicale specialite, String email, 
                                String ville, boolean actif) {
        Medecin medecin = new Medecin();
        medecin.setUserId(++userIdCounter); // Unique userId
        medecin.setNom(nom);
        medecin.setPrenom(prenom);
        medecin.setNumeroRPPS(rpps);
        medecin.setSpecialite(specialite);
        medecin.setEmail(email);
        medecin.setTelephone("0123456789");
        medecin.setAdresseCabinet("123 Avenue Medicale");
        medecin.setCodePostalCabinet("75001");
        medecin.setVilleCabinet(ville);
        medecin.setDureeConsultationDefaut(30);
        medecin.setTarif(new BigDecimal("50.00"));
        medecin.setConventionne(true);
        medecin.setCarteVitaleAcceptee(true);
        medecin.setActif(actif);
        medecin.setDateCreation(LocalDateTime.now());
        return medecin;
    }
}