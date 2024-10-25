package project.demo.dto.user;

import jakarta.validation.constraints.NotNull;
import project.demo.model.Role;

public record UserUpdateRoleDto(
        @NotNull
        Role.RoleName roleName
) {
}
