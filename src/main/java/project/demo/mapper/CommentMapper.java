package project.demo.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import project.demo.config.MapperConfig;
import project.demo.dto.comment.CommentDto;
import project.demo.model.Comment;

@Mapper(config = MapperConfig.class)
public interface CommentMapper {
    @Mapping(target = "taskName", source = "task.name")
    @Mapping(target = "username", source = "user.username")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(Page<Comment> commentList);
}
