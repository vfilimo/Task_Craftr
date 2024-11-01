package project.demo.exception;

public class TaskDueDateException extends RuntimeException {
    public TaskDueDateException(String message) {
        super(message);
    }
}
