package project.demo.validation.end.after.start;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import project.demo.dto.project.ProjectRequestCreateDto;
import project.demo.model.Project;

public class EndDateAfterStartDateValidator
        implements ConstraintValidator<EndDateAfterStartDate, ProjectRequestCreateDto> {
    @Override
    public void initialize(EndDateAfterStartDate constraintAnnotation) {}

    @Override
    public boolean isValid(ProjectRequestCreateDto project, ConstraintValidatorContext context) {
        if (project.startDate() == null || project.endDate() == null) {
            return true;
        }
        return project.endDate().isAfter(project.startDate());
    }
}
