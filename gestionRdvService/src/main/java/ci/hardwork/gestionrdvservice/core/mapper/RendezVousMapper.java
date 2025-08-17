package ci.hardwork.gestionrdvservice.core.mapper;

import ci.hardwork.gestionrdvservice.core.dto.RendezVousCreateDTO;
import ci.hardwork.gestionrdvservice.core.dto.RendezVousResponseDTO;
import ci.hardwork.gestionrdvservice.core.models.RendezVous;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RendezVousMapper {
    
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "medecin", ignore = true)
    RendezVous toEntity(RendezVousCreateDTO rendezVousCreateDTO);
    
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.nom", target = "patientNom")
    @Mapping(source = "patient.prenom", target = "patientPrenom")
    @Mapping(source = "medecin.id", target = "medecinId")
    @Mapping(source = "medecin.nom", target = "medecinNom")
    @Mapping(source = "medecin.prenom", target = "medecinPrenom")
    @Mapping(source = "medecin.specialite", target = "medecinSpecialite")
    RendezVousResponseDTO toResponseDTO(RendezVous rendezVous);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "medecin", ignore = true)
    void updateEntityFromDTO(RendezVousCreateDTO rendezVousUpdateDTO, @MappingTarget RendezVous rendezVous);
}