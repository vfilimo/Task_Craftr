package project.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import project.demo.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"user", "task"})
    Page<Comment> findCommentByTaskId(Long taskId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "task"})
    Page<Comment> findCommentByTaskIdAndUserId(Long taskId, Long id, Pageable pageable);
}
