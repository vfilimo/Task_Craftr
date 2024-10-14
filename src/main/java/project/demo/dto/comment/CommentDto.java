package project.demo.dto.comment;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String taskName,
        String username,
        String text,
        LocalDateTime timestamp
) {
}
