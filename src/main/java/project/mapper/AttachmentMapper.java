package project.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import project.config.MapperConfig;
import project.dto.attachment.AttachmentDto;
import project.model.Attachment;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    @Mapping(target = "taskName", source = "task.name")
    AttachmentDto toDto(Attachment attachment);

    List<AttachmentDto> toDto(Page<Attachment> attachmentPage);
}
