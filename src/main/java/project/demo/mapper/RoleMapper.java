package project.demo.mapper;

import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import project.demo.config.MapperConfig;
import project.demo.model.Role;

@Mapper(config = MapperConfig.class)
public interface RoleMapper {
    @Named("fromRolesToRolesName")
    default List<String> fromRolesToRolesName(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .map(Role.RoleName::name)
                .toList();
    }
}
