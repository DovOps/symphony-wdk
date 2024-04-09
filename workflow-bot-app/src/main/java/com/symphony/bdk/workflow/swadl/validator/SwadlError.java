package com.symphony.bdk.workflow.swadl.validator;

import lombok.Value;

@Value
public class SwadlError {
  int lineNumber;
  String message;

  @Override
  public String toString() {
    if (lineNumber >= 0) {
      return "Line %s: %s".formatted(lineNumber, message);
    } else {
      return message;
    }
  }

}
