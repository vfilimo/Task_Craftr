package project.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.demo.config.MapperConfig;
import project.demo.dto.attachment.AttachmentDto;
import project.demo.model.Attachment;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    @Mapping(target = "taskName", source = "task.name")
    AttachmentDto toDto(Attachment attachment);
}
