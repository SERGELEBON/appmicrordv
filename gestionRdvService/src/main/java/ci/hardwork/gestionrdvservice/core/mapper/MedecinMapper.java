package ci.hardwork.gestionrdvservice.core.mapper;

import ci.hardwork.gestionrdvservice.core.dto.MedecinCreateDTO;
import ci.hardwork.gestionrdvservice.core.dto.MedecinResponseDTO;
import ci.hardwork.gestionrdvservice.core.models.Medecin;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MedecinMapper {
    
    Medecin toEntity(MedecinCreateDTO medecinCreateDTO);
    
    MedecinResponseDTO toResponseDTO(Medecin medecin);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(MedecinCreateDTO medecinUpdateDTO, @MappingTarget Medecin medecin);
}