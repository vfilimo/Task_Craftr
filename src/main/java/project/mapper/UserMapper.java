package project.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import project.config.MapperConfig;
import project.dto.user.UserRegistrationRequestDto;
import project.dto.user.UserResponseDto;
import project.dto.user.UserUpdateRequestDto;
import project.model.User;

@Mapper(config = MapperConfig.class, uses = RoleMapper.class)
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserRegistrationRequestDto userRegistrationRequestDto);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "fromRolesToRolesName")
    UserResponseDto toDto(User user);

    @Mappings({@Mapping(target = "id", ignore = true),
            @Mapping(target = "roles", ignore = true),
            @Mapping(target = "username", ignore = true)})
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequestDto userUpdateRequestDto);
}
