package com.symphony.bdk.workflow.swadl.exception;

public class NoStartingEventException extends RuntimeException {
  public NoStartingEventException(String workflowId) {
    super("Workflow with id \"%s\" does not have any starting event.".formatted(workflowId));
  }
}
