package com.example.taskrestapi.controller;

import com.example.taskrestapi.enums.TaskStatus;
import com.example.taskrestapi.service.TaskService;
import com.example.taskrestapi.transportObject.TaskJsonBody;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/taskmanager/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @Operation(summary = "Get tasks assigned to a user by their ID", description = "Get tasks assigned to a user by their ID")
    @GetMapping("/getTasksByAssignee")
    public List<TaskJsonBody> getTasksByAssigneeUser(@RequestParam Long id,
                                                     @RequestParam("offset") Integer offset,
                                                     @RequestParam("limit") Integer limit) {
        return taskService.getTasksByAssigneeUser(id, PageRequest.of(offset, limit));
    }

    @Operation(summary = "Get tasks created by a user by their ID", description = "Get tasks created by a user by their ID")
    @GetMapping("/getTasksByReporter")
    public List<TaskJsonBody> getTasksByReporterUser(@RequestParam Long id,
                                                     @RequestParam("offset") Integer offset,
                                                     @RequestParam("limit") Integer limit) {
        return taskService.getTaskByReporter(id, PageRequest.of(offset, limit));
    }

    @Operation(summary = "Create a new task", description = "Create a new task")
    @PostMapping("/add")
    public TaskJsonBody createTask(
            @Valid @RequestBody TaskJsonBody task) {
        try {
            return taskService.createTask(task);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Update task by reporter", description = "Update task details by reporter")
    @PostMapping("/update")
    public TaskJsonBody updateTask(@Valid @RequestBody TaskJsonBody taskJsonBody) {
        try {
            return taskService.updateTaskByReporter(taskJsonBody);
        } catch (EntityNotFoundException | IllegalAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "\"Update task status by ID", description = "Update task status by ID")
    @GetMapping("/updateStatus")
    public TaskJsonBody updateStatus(@RequestParam Long id, @RequestParam TaskStatus status) {
        try {
            return taskService.updateStatus(id, status);
        } catch (EntityNotFoundException | IllegalAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Delete task by ID", description = "Delete task by ID")
    @DeleteMapping("/delete")
    public void deleteTask(@RequestParam Long id) {
        try {
            taskService.deleteTask(id);
        } catch (EntityNotFoundException | IllegalAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
