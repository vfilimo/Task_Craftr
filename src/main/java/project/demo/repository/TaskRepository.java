package project.demo.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.demo.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findTaskByProjectIdAndAssigneeId(Long assigneeId,
                                                Long projectId, Pageable pageable);

    Optional<Task> findTaskByIdAndAssigneeId(Long taskId, Long assigneeId);
}
