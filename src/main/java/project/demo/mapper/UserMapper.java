package project.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.demo.config.MapperConfig;
import project.demo.dto.user.UserRegistrationRequestDto;
import project.demo.dto.user.UserResponseDto;
import project.demo.model.User;

@Mapper(config = MapperConfig.class, uses = RoleMapper.class)
public interface UserMapper {
    User toEntity(UserRegistrationRequestDto userRegistrationRequestDto);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "fromRolesToRolesName")
    UserResponseDto toDto(User user);
}
