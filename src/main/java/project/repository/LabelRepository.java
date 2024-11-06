package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.model.Label;

public interface LabelRepository extends JpaRepository<Label, Long> {
}
