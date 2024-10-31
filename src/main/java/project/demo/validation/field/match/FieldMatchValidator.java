package project.demo.validation.field.match;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        Object field = new BeanWrapperImpl(obj).getPropertyValue(this.firstFieldName);
        Object fieldMatch = new BeanWrapperImpl(obj).getPropertyValue(this.secondFieldName);
        return Objects.equals(field, fieldMatch);
    }
}
