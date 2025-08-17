package ci.hardwork.authservice.core.mapper;

import ci.hardwork.authservice.core.dto.UserDto;
import ci.hardwork.authservice.core.dto.RegisterRequest;
import ci.hardwork.authservice.core.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "authorities", expression = "java(user.getAuthorities() != null ? user.getAuthorities().stream().map(auth -> auth.getAuthority()).toList() : java.util.List.of())")
    UserDto toDto(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isEnabled", constant = "true")
    @Mapping(target = "isAccountNonExpired", constant = "true")
    @Mapping(target = "isAccountNonLocked", constant = "true")
    @Mapping(target = "isCredentialsNonExpired", constant = "true")
    @Mapping(target = "authorities", ignore = true)
    User toEntity(RegisterRequest registerRequest);
}