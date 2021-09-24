package com.symphony.bdk.workflow.engine.executor.user;

import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.workflow.engine.executor.ActivityExecutor;
import com.symphony.bdk.workflow.engine.executor.ActivityExecutorContext;
import com.symphony.bdk.workflow.swadl.v1.activity.user.GetUsers;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GetUsersExecutor implements ActivityExecutor<GetUsers> {

  private static final String OUTPUT_USERS_KEY = "users";

  @Override
  public void execute(ActivityExecutorContext<GetUsers> context) {

    log.debug("Searching users");
    GetUsers getUsers = context.getActivity();

    List<UserV2> users = null;

    // Since the workflow is validated by swadl-schema, at least one of the following attributes is not null
    if (getUsers.getUsernames() != null) {
      users = context.bdk().users().listUsersByUsernames(getUsers.getUsernames().get(), getUsers.getActive().get());

    } else if (getUsers.getUserIds() != null) {
      users = context.bdk()
          .users()
          .listUsersByIds(toLongs(getUsers.getUserIds().get()), getUsers.getLocal().get(), getUsers.getActive().get());

    } else if (getUsers.getEmails() != null) {
      users = context.bdk()
          .users()
          .listUsersByEmails(getUsers.getEmails().get(), getUsers.getLocal().get(), getUsers.getActive().get());

    }

    context.setOutputVariable(OUTPUT_USERS_KEY, users);
  }

  private List<Long> toLongs(List<Number> ids) {
    return ids.stream().map(Number::longValue).collect(Collectors.toList());
  }

}
