package project.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Task t "
            + "JOIN t.assignee u JOIN t.project p WHERE u.id = :userId")
    Page<Project> findAllProjectsForUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Task t "
            + "JOIN t.assignee u JOIN t.project p WHERE u.id = :userId AND p.id = :projectId")
    Optional<Project> findProjectByUserIdAndProjectId(@Param("userId") Long userId,
                                                     @Param("projectId") Long projectId);
}
