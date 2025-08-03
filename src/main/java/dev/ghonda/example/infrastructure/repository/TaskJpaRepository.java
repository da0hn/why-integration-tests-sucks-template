package dev.ghonda.example.infrastructure.repository;

import dev.ghonda.example.core.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskJpaRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByExternalId(String externalId);

}
