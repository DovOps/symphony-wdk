package com.symphony.bdk.workflow.swadl.v1.activity.message;

import com.symphony.bdk.workflow.swadl.v1.activity.BaseActivity;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateMessage extends BaseActivity {
  private String messageId;
  @Nullable private String template;
  @Nullable private String templatePath;
  @Nullable private String content;
  private Boolean silent = Boolean.TRUE;

  public void setContent(Object content) {
    if (content instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, String> map = (Map<String, String>) content;
      setTemplate(map.get("template"));
      setTemplatePath(map.get("template-path"));
    } else if (content instanceof String string) {
      this.content = string;
    }
  }
}
