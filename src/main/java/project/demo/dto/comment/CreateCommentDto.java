package project.demo.dto.comment;

public record CreateCommentDto(
        String taskId,
        String text
) {
}
