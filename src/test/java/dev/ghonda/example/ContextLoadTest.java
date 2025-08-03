package dev.ghonda.example;

import dev.ghonda.example.configuration.PostgreSQLContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(PostgreSQLContainerConfiguration.class)
class ContextLoadTest {

    @Test
    void contextLoads() {
    }

}
