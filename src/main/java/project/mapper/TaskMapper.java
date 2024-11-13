package project.mapper;

import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import project.config.MapperConfig;
import project.dto.task.AssigneeTaskCreateDto;
import project.dto.task.TaskCreateDto;
import project.dto.task.TaskDto;
import project.dto.task.TaskUpdateDto;
import project.model.Label;
import project.model.Task;

@Mapper(config = MapperConfig.class, uses = LabelMapper.class)
public interface TaskMapper {
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    Task toEntity(TaskCreateDto taskCreateDto);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    Task toEntity(AssigneeTaskCreateDto assigneeTaskCreateDto);

    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "assigneeUsername", source = "assignee.username")
    @Mapping(target = "labelsId", ignore = true)
    TaskDto toDto(Task task);

    default Page<TaskDto> toDto(Page<Task> taskPage) {
        return new PageImpl<>(
                taskPage.stream().map(this::toDto).toList(),
                taskPage.getPageable(),
                taskPage.getTotalElements());
    }

    @AfterMapping
    default void setLabelsIds(@MappingTarget TaskDto taskDto, Task task) {
        if (task.getLabels() == null) {
            return;
        }
        List<Long> listLabelIds = task.getLabels().stream()
                .map(Label::getId)
                .toList();
        taskDto.setLabelsId(listLabelIds);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", source = "labelsIds", qualifiedByName = "toLabelsList")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTask(@MappingTarget Task task, TaskUpdateDto taskUpdateDto);
}
