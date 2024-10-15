package project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.demo.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
