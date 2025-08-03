package dev.ghonda.example.core.command;

import dev.ghonda.example.core.domain.Priority;

public record NewTaskCommand(
    String externalId,
    String title,
    String description,
    Priority priority
) {
}
