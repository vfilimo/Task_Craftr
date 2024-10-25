package project.demo.dto.comment;

public record CreateCommentDto(
        Long taskId,
        String text
) {
}
