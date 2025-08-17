package ci.hardwork.gestionrdvservice.core.mapper;

import ci.hardwork.gestionrdvservice.core.dto.PatientCreateDTO;
import ci.hardwork.gestionrdvservice.core.dto.PatientResponseDTO;
import ci.hardwork.gestionrdvservice.core.dto.PatientUpdateDTO;
import ci.hardwork.gestionrdvservice.core.models.Patient;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    
    Patient toEntity(PatientCreateDTO patientCreateDTO);
    
    PatientResponseDTO toResponseDTO(Patient patient);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(PatientUpdateDTO patientUpdateDTO, @MappingTarget Patient patient);
}