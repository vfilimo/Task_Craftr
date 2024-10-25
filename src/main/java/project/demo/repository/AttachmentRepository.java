package project.demo.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;
import project.demo.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    @EntityGraph(attributePaths = "task")
    @Query("SELECT att FROM Attachment att JOIN att.task t JOIN t.assignee a "
            + "WHERE t.id = :taskId AND a.id = :assigneeId")
    Page<Attachment> findAllByTaskIdAndAssigneeId(@PathVariable("taskId") Long taskId,
                                                  @PathVariable("assigneeId") Long assigneeId,
                                                  Pageable pageable);

    @Query("SELECT att FROM Attachment att JOIN FETCH att.task t JOIN t.assignee a "
            + "WHERE att.id = :attachmentId AND a.id = :assigneeId")
    Optional<Attachment> findAttachmentByIdAndAssigneeId(
            @PathVariable("attachmentId") Long attachmentId,
            @PathVariable("assigneeId") Long assigneeId);

    @EntityGraph(attributePaths = "task")
    Page<Attachment> findAllByTaskId(Long taskId, Pageable pageable);
}
