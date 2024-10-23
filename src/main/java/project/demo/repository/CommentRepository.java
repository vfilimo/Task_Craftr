package project.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import project.demo.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"user", "task"})
    List<Comment> findCommentByTaskId(Long taskId);

    @EntityGraph(attributePaths = {"user", "task"})
    List<Comment> findCommentByTaskIdAndUserId(Long taskId, Long id);
}
