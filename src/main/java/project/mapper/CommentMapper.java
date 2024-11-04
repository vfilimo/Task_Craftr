package project.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import project.config.MapperConfig;
import project.dto.comment.CommentDto;
import project.model.Comment;

@Mapper(config = MapperConfig.class)
public interface CommentMapper {
    @Mapping(target = "taskName", source = "task.name")
    @Mapping(target = "username", source = "user.username")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(Page<Comment> commentList);
}
