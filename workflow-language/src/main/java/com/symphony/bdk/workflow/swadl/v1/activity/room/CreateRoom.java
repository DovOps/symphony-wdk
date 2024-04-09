package com.symphony.bdk.workflow.swadl.v1.activity.room;

import com.symphony.bdk.workflow.swadl.v1.activity.OboActivity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://developers.symphony.com/restapi/reference#create-room-v3">Create room API</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CreateRoom extends OboActivity {
  @Nullable private String roomName;
  @Nullable private String roomDescription;
  @Nullable private Map<String, String> keywords;
  @Nullable private String subType;

  private List<Long> userIds;
  private Boolean membersCanInvite;
  private Boolean discoverable;
  private Boolean readOnly;
  private Boolean copyProtected;
  private Boolean crossPod;
  private Boolean viewHistory;
  private Boolean multilateralRoom;

  @JsonProperty("public")
  private Boolean isPublic;

  @JsonIgnore
  @Nullable
  public List<Long> getUserIdsAsLongs() {
    return userIds;
  }

  @Data
  public static class KeywordItem {
    private String key;
    private String value;
  }
}
