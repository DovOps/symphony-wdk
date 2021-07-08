package com.symphony.bdk.workflow.engine.camunda.bpmn;

import com.symphony.bdk.workflow.engine.camunda.CamundaExecutor;
import com.symphony.bdk.workflow.lang.exception.NoCommandToStartException;
import com.symphony.bdk.workflow.lang.exception.NoStartingEventException;
import com.symphony.bdk.workflow.lang.swadl.Activity;
import com.symphony.bdk.workflow.lang.swadl.Event;
import com.symphony.bdk.workflow.lang.swadl.Workflow;
import com.symphony.bdk.workflow.lang.swadl.activity.BaseActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.xml.ModelValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

@Component
public class CamundaBpmnBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(CamundaBpmnBuilder.class);

  private static final String OUTPUT_BPMN_FILE_NAME = "./output.bpmn";

  private final RepositoryService repositoryService;

  // run a single workflow at anytime
  private Deployment deploy;

  @Autowired
  public CamundaBpmnBuilder(RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  public void generateBpmnOutputFile(Workflow workflow) throws IOException {
    BpmnModelInstance instance = workflowToBpmn(workflow);

    File file = new File(OUTPUT_BPMN_FILE_NAME);
    if (file.exists()) {
      LOGGER.info("Output bpmn file {} already exists. It will be overridden.", OUTPUT_BPMN_FILE_NAME);
    } else {
      boolean successfullyCreated = file.createNewFile();
      String logMessage = successfullyCreated
          ? String.format("Output bpmn file %s is created.", OUTPUT_BPMN_FILE_NAME)
          : String.format("Output bpmn file %s is NOT created.", OUTPUT_BPMN_FILE_NAME);
      LOGGER.info(logMessage);
    }

    try {
      Bpmn.writeModelToFile(file, instance);
      LOGGER.info("Output bpmn file {} is updated.", OUTPUT_BPMN_FILE_NAME);
    } catch (BpmnModelException | ModelValidationException e) {
      LOGGER.error(e.getMessage());
    }
  }

  public void addWorkflow(Workflow workflow) {
    BpmnModelInstance instance = workflowToBpmn(workflow);

    if (deploy != null) {
      repositoryService.deleteDeployment(deploy.getId());
    }
    deploy = repositoryService.createDeployment()
        .addModelInstance(workflow.getName() + ".bpmn", instance)
        .deploy();
  }

  private Optional<Event> getStartingEvent(Workflow workflow) {
    if (workflow.getFirstActivity().isPresent()) {
      Activity firstActivity = workflow.getFirstActivity().get();
      return firstActivity.getEvent();
    }
    return Optional.empty();
  }

  private String getCommandToStart(Workflow workflow) {
    Optional<Event> startingEvent = getStartingEvent(workflow);

    if (startingEvent.isEmpty()) {
      throw new NoStartingEventException();
    }

    Optional<String> commandToStart = startingEvent.get().getCommand();

    if (commandToStart.isPresent()) {
      return commandToStart.get();
    }

    throw new NoCommandToStartException();
  }

  @SneakyThrows
  private BpmnModelInstance workflowToBpmn(Workflow workflow) {
    ProcessBuilder process = Bpmn.createExecutableProcess(workflow.getName());

    String commandToStart = getCommandToStart(workflow);
    AbstractFlowNodeBuilder eventBuilder = process.startEvent().message("message_" + commandToStart);

    for (Activity activity : workflow.getActivities()) {
      Optional<BaseActivity<?>> maybeActivity = activity.getActivity();
      if (maybeActivity.isPresent()) {
        BaseActivity<?> baseActivity = maybeActivity.get();

        Type executorType =
            ((ParameterizedType) (baseActivity.getClass().getGenericSuperclass())).getActualTypeArguments()[0];
        eventBuilder = eventBuilder.serviceTask()
            .camundaClass(CamundaExecutor.class)
            .name(baseActivity.getName())
            .camundaInputParameter(CamundaExecutor.IMPL, executorType.getTypeName())
            .camundaInputParameter(CamundaExecutor.ACTIVITY, new ObjectMapper().writeValueAsString(baseActivity));
      }
    }

    return eventBuilder.endEvent().done();
  }

}
