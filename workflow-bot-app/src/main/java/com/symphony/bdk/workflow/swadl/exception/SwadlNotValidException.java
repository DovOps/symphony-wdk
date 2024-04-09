package com.symphony.bdk.workflow.swadl.exception;

import com.symphony.bdk.workflow.swadl.validator.SwadlError;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * An exception to be thrown when validating an invalid SWADL definition.
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class SwadlNotValidException extends IOException {
  transient List<SwadlError> errors;

  public SwadlNotValidException(List<SwadlError> errors, String fullDetails) {
    super("SWADL content is not valid: %s, full details: %s".formatted(errors, fullDetails));
    this.errors = errors;
  }

  public SwadlNotValidException(JsonProcessingException exception) {
    super("SWADL content is not valid YAML at line %d, full details: %s".formatted(
        exception.getLocation().getLineNr(), exception.getMessage()));
    this.errors =
        Collections.singletonList(new SwadlError(exception.getLocation().getLineNr(), exception.getMessage()));
  }
}
