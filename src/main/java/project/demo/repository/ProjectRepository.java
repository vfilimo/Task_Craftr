package project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.demo.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
