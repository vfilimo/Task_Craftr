package project.demo.mapper;

import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import project.demo.config.MapperConfig;
import project.demo.dto.task.AssigneeTaskCreateDto;
import project.demo.dto.task.TaskCreateDto;
import project.demo.dto.task.TaskDto;
import project.demo.dto.task.TaskUpdateDto;
import project.demo.model.Label;
import project.demo.model.Task;

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

    List<TaskDto> toDto(Page<Task> taskPage);

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
