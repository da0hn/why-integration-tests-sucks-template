package dev.ghonda.example.infrastructure.rest.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SimpleResource implements Resource {

  Long id;

  public static SimpleResource of(final Long id) {
    return new SimpleResource(id);
  }

}
