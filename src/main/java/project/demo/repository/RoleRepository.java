package project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.demo.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(Role.RoleName roleName);
}
