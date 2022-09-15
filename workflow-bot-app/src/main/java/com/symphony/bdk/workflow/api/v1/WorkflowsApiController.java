package com.symphony.bdk.workflow.api.v1;

import com.symphony.bdk.workflow.api.v1.dto.ErrorResponse;
import com.symphony.bdk.workflow.api.v1.dto.WorkflowActivitiesView;
import com.symphony.bdk.workflow.api.v1.dto.WorkflowDefinitionView;
import com.symphony.bdk.workflow.api.v1.dto.WorkflowExecutionRequest;
import com.symphony.bdk.workflow.api.v1.dto.WorkflowInstView;
import com.symphony.bdk.workflow.api.v1.dto.WorkflowView;
import com.symphony.bdk.workflow.engine.ExecutionParameters;
import com.symphony.bdk.workflow.engine.WorkflowEngine;
import com.symphony.bdk.workflow.monitoring.service.MonitoringService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api("Api to execute and monitor workflows")
@RequestMapping("v1/workflows")
@Slf4j
public class WorkflowsApiController {

  private final MonitoringService monitoringService;
  private final WorkflowEngine workflowEngine;

  public WorkflowsApiController(WorkflowEngine workflowEngine, MonitoringService monitoringService) {
    this.workflowEngine = workflowEngine;
    this.monitoringService = monitoringService;
  }

  @ApiOperation("Triggers the execution of a workflow given by its id. This is an asynchronous operation.")
  @ApiResponses(value = {@ApiResponse(code = 204, message = "", response = Object.class),
      @ApiResponse(code = 404, message = "No workflow found with id {id}", response = ErrorResponse.class),
      @ApiResponse(code = 401, message = "Request token is not valid", response = ErrorResponse.class)})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PostMapping("/{id}/execute")
  public ResponseEntity<Object> executeWorkflowById(
      @ApiParam("Workflow's token to authenticate the request") @RequestHeader(name = "X-Workflow-Token") String token,
      @ApiParam("Workflow's id that is provided in SWADL") @PathVariable String id,
      @ApiParam("Arguments to be passed to the event triggering the workflow") @RequestBody
          WorkflowExecutionRequest arguments) {

    log.info("Executing workflow {}", id);
    workflowEngine.execute(id, new ExecutionParameters(arguments.getArgs(), token));

    return ResponseEntity.noContent().build();
  }

  @ApiOperation("List all deployed workflows")
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "OK", response = WorkflowView.class, responseContainer = "List")})
  @GetMapping("/")
  public ResponseEntity<List<WorkflowView>> listAllWorkflows() {
    return ResponseEntity.ok(monitoringService.listAllWorkflows());
  }

  @ApiOperation("List all instances of a given workflow")
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "OK", response = WorkflowInstView.class, responseContainer = "List")})
  @GetMapping("/{workflowId}/instances")
  public ResponseEntity<List<WorkflowInstView>> listWorkflowInstances(@PathVariable String workflowId) {
    return ResponseEntity.ok(monitoringService.listWorkflowInstances(workflowId));
  }

  @ApiOperation("List the completed activities in a given instance for a given workflow")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = WorkflowActivitiesView.class),
      @ApiResponse(code = 404,
          message = "Either no workflow deployed with id {workflowId} is found or the instance id {instanceId} "
              + "is not correct",
          response = ErrorResponse.class)})
  @GetMapping("/{workflowId}/instances/{instanceId}/activities")
  public ResponseEntity<WorkflowActivitiesView> listInstanceActivities(@PathVariable String workflowId,
      @PathVariable String instanceId) {
    return ResponseEntity.ok(monitoringService.listWorkflowInstanceActivities(workflowId, instanceId));
  }

  @ApiOperation("List activities definitions for a given workflow")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = WorkflowDefinitionView.class),
      @ApiResponse(code = 404, message = "No workflow deployed with id {Id} is found", response = ErrorResponse.class)})
  @GetMapping("/{workflowId}/definitions")
  public ResponseEntity<WorkflowDefinitionView> listWorkflowActivities(@PathVariable String workflowId) {
    return ResponseEntity.ok(monitoringService.getWorkflowDefinition(workflowId));
  }

}
