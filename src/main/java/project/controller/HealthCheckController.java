package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Tag(name = "Health Check", description = "Endpoints  for health check.")
public class HealthCheckController {
    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    @Operation(summary = "Application health check controller")
    public String healthCheckStatus() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return "Application is running and connected to the database.";
        } catch (Exception e) {
            return "Application is down or not connected to the database.";
        }
    }
}
