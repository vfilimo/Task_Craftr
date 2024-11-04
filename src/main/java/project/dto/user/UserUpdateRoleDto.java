package project.dto.user;

import jakarta.validation.constraints.NotNull;
import project.model.Role;

public record UserUpdateRoleDto(
        @NotNull
        Role.RoleName roleName
) {
}
