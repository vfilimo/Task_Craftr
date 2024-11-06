package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(Role.RoleName roleName);
}
