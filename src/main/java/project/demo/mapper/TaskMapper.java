package project.demo.mapper;

import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import project.demo.config.MapperConfig;
import project.demo.dto.task.TaskCreateDto;
import project.demo.dto.task.TaskDto;
import project.demo.dto.task.TaskUpdateDto;
import project.demo.model.Label;
import project.demo.model.Task;

@Mapper(config = MapperConfig.class)
public interface TaskMapper {
    Task toEntity(TaskCreateDto taskCreateDto);

    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "assigneeUsername", source = "assignee.username")
    @Mapping(target = "labelsId", ignore = true)
    TaskDto toDto(Task task);

    List<TaskDto> toDto(Page<Task> taskPage);

    @Mapping(target = "id", ignore = true)
    void updateTask(@MappingTarget Task task, TaskUpdateDto taskUpdateDto);

    @AfterMapping
    default void setLabelsIds(@MappingTarget TaskDto taskDto, Task task) {
        List<Long> listLabelIds = task.getLabels().stream()
                .map(Label::getId)
                .toList();
        taskDto.labelsId().addAll(listLabelIds);
    }
}
