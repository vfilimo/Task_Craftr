package project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import project.config.MapperConfig;
import project.dto.attachment.AttachmentDto;
import project.model.Attachment;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    @Mapping(target = "taskName", source = "task.name")
    AttachmentDto toDto(Attachment attachment);

    default Page<AttachmentDto> toDto(Page<Attachment> attachmentPage) {
        return new PageImpl<>(
                attachmentPage.stream().map(this::toDto).toList(),
                attachmentPage.getPageable(),
                attachmentPage.getTotalElements());
    }
}
