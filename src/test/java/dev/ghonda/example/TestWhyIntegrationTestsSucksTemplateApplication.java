package dev.ghonda.example;

import org.springframework.boot.SpringApplication;

public class TestWhyIntegrationTestsSucksTemplateApplication {

    public static void main(final String[] args) {
        SpringApplication.from(Application::main)
            .with(TestcontainersConfiguration.class)
            .run(args);
    }

}
