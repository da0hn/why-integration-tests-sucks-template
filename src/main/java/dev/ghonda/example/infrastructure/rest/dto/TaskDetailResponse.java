package dev.ghonda.example.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.ghonda.example.core.domain.Priority;
import dev.ghonda.example.core.domain.Status;
import dev.ghonda.example.core.domain.Task;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TaskDetailResponse(
    Long id,
    String externalId,
    String title,
    String description,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime updatedAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime dueDate,
    Status status,
    Priority priority
) {

    public static TaskDetailResponse of(final Task task) {
        return TaskDetailResponse.builder()
            .id(task.getId())
            .externalId(task.getExternalId())
            .title(task.getTitle())
            .description(task.getDescription())
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .dueDate(task.getDueDate())
            .status(task.getStatus())
            .priority(task.getPriority())
            .build();
    }

}
