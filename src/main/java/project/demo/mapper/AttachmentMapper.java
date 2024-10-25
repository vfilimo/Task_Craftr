package project.demo.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import project.demo.config.MapperConfig;
import project.demo.dto.attachment.AttachmentDto;
import project.demo.model.Attachment;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    @Mapping(target = "taskName", source = "task.name")
    AttachmentDto toDto(Attachment attachment);

    List<AttachmentDto> toDto(Page<Attachment> attachmentPage);
}
