package ci.hardwork.gestionrdvservice.core.mapper;

import ci.hardwork.gestionrdvservice.core.dto.CreneauDisponibiliteDTO;
import ci.hardwork.gestionrdvservice.core.models.CreneauDisponibilite;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CreneauMapper {
    
    @Mapping(source = "medecin.id", target = "medecinId")
    @Mapping(source = "medecin.nom", target = "medecinNom")
    @Mapping(source = "medecin.prenom", target = "medecinPrenom")
    CreneauDisponibiliteDTO toDTO(CreneauDisponibilite creneauDisponibilite);
    
    @Mapping(target = "medecin", ignore = true)
    CreneauDisponibilite toEntity(CreneauDisponibiliteDTO creneauDTO);
}