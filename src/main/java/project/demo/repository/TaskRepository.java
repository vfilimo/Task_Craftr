package project.demo.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import project.demo.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @EntityGraph(attributePaths = {"assignee", "project", "labels"})
    Page<Task> findTaskByProjectIdAndAssigneeId(Long projectId,
                                                Long assigneeId, Pageable pageable);

    @EntityGraph(attributePaths = {"assignee", "project", "labels"})
    Optional<Task> findTaskByIdAndAssigneeId(Long taskId, Long assigneeId);

    @EntityGraph(attributePaths = {"assignee", "project", "labels"})
    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"assignee", "project", "labels"})
    Optional<Task> findById(Long taskId);
}
