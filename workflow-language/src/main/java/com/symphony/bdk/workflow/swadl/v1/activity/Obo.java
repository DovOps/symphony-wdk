package com.symphony.bdk.workflow.swadl.v1.activity;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class Obo {
  @Nullable private String username;
  @Nullable private Long userId;
}
