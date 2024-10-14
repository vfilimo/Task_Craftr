package project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.demo.model.Label;

public interface LabelRepository extends JpaRepository<Label, Long> {
}
