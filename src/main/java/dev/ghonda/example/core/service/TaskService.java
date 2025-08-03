package dev.ghonda.example.core.service;

import dev.ghonda.example.core.command.NewTaskCommand;
import dev.ghonda.example.core.domain.Task;
import org.springframework.transaction.annotation.Transactional;

public interface TaskService {

    @Transactional
    Task newTask(NewTaskCommand command);

    Task findTaskById(String externalId);

}
