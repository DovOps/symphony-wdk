package com.symphony.bdk.workflow.swadl.exception;

/**
 * An exception to be thrown when an invalid gateway is encountered in the BPMN.
 */
public class InvalidActivityException extends RuntimeException {
  public InvalidActivityException(String workflowId, String message) {
    super("Invalid activity in the workflow %s: %s".formatted(workflowId, message));
  }
}
