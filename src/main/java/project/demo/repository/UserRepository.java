package project.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByUsernameOrEmail(String username, String email);
}
