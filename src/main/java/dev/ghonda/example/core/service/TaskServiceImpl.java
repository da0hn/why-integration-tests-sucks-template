package dev.ghonda.example.core.service;

import dev.ghonda.example.core.command.NewTaskCommand;
import dev.ghonda.example.core.domain.Task;
import dev.ghonda.example.core.exceptions.DomainValidationException;
import dev.ghonda.example.core.exceptions.ResourceNotFoundException;
import dev.ghonda.example.infrastructure.repository.TaskJpaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskJpaRepository taskJpaRepository;

    @Override
    @Transactional
    public Task newTask(final NewTaskCommand command) {
        if (log.isInfoEnabled()) { log.info("Creating new task with command: {}", command); }
        if (log.isDebugEnabled()) { log.debug("m=newTask(command: {})", command); }

        this.taskJpaRepository.findByExternalId(command.externalId())
            .ifPresent(task -> {
                if (log.isWarnEnabled()) {
                    log.warn("Task with externalId {} already exists: {}", command.externalId(), task);
                }
                throw new DomainValidationException("Task with externalId " + command.externalId() + " already exists");
            });

        final var newTask = Task.newTask(command);

        this.taskJpaRepository.save(newTask);

        if (log.isInfoEnabled()) { log.info("Task created with id: {}", newTask.getId()); }

        return newTask;
    }

    @Override
    public Task findTaskById(final String externalId) {
        return this.taskJpaRepository.findByExternalId(externalId)
            .orElseThrow(() -> new ResourceNotFoundException("Task with externalId " + externalId + " not found"));
    }

    @Override
    public List<Task> findAllTasks() {
        return this.taskJpaRepository.findAll();
    }

}
