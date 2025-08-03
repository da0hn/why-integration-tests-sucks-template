package dev.ghonda.example.infrastructure.rest;

import dev.ghonda.example.core.command.NewTaskCommand;
import dev.ghonda.example.core.service.TaskService;
import dev.ghonda.example.infrastructure.rest.dto.TaskDetailResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/tasks")
public class TaskRestController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDetailResponse> newTask(@RequestBody final NewTaskCommand command) {
        final var task = this.taskService.newTask(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskDetailResponse.of(task));
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<TaskDetailResponse> getTaskById(@PathVariable final String externalId) {
        final var task = this.taskService.findTaskById(externalId);
        return ResponseEntity.ok(TaskDetailResponse.of(task));
    }


}
