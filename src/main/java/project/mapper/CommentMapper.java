package project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import project.config.MapperConfig;
import project.dto.comment.CommentDto;
import project.model.Comment;

@Mapper(config = MapperConfig.class)
public interface CommentMapper {
    @Mapping(target = "taskName", source = "task.name")
    @Mapping(target = "username", source = "user.username")
    CommentDto toDto(Comment comment);

    default Page<CommentDto> toDto(Page<Comment> commentPage) {
        return new PageImpl<>(
                commentPage.stream().map(this::toDto).toList(),
                commentPage.getPageable(),
                commentPage.getTotalElements());
    }
}
