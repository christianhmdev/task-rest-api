package com.example.taskrestapi.service;

import com.example.taskrestapi.enums.TaskStatus;
import com.example.taskrestapi.model.Task;
import com.example.taskrestapi.model.User;
import com.example.taskrestapi.repository.TaskRepository;
import com.example.taskrestapi.repository.UserRepository;
import com.example.taskrestapi.security.SecurityContextHolderFacade;
import com.example.taskrestapi.transportObject.TaskJsonBody;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SecurityContextHolderFacade securityContextHolderFacade;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, SecurityContextHolderFacade securityContextHolderFacade) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.securityContextHolderFacade = securityContextHolderFacade;
    }

    public List<TaskJsonBody> getTasksByAssigneeUser(Long id, PageRequest pageRequest) {
        Page<Task> tasks = taskRepository.getTasksByAssigneeUser(id, pageRequest);
        return tasks.stream().map(t -> convertToTaskJsonBody(t)).toList();
    }

    public List<TaskJsonBody> getTaskByReporter(Long id, PageRequest pageRequest) {
        Page<Task> tasks = taskRepository.getTasksByReporter(id, pageRequest);
        return tasks.stream().map(t -> convertToTaskJsonBody(t)).toList();
    }

    public TaskJsonBody createTask(TaskJsonBody taskJsonBody) throws EntityNotFoundException {

        Task task = convertToTask(taskJsonBody);

        //find reporter user
        UserDetails userDetails = securityContextHolderFacade.getAuthenticateUser();
        String username = userDetails.getUsername();
        User reporter = userRepository.findByUsername(username).get();
        task.setUserReporter(reporter);
        taskJsonBody.setReporter(reporter.getId());


        //if assignee isn't specified then assignee = reporter
        if (taskJsonBody.getAssignee() == null) {
            task.setUserAssignee(reporter);
            taskJsonBody.setAssignee(reporter.getId());
        } else {
            Optional<User> assignee = userRepository.findById(taskJsonBody.getAssignee());
            if (assignee.isEmpty()) {
                throw new EntityNotFoundException("User with id = " + taskJsonBody.getAssignee() + " doesn't exist");
            }
            task.setUserAssignee(assignee.get());
        }


        taskRepository.save(task);
        Example<Task> exampleTask = Example.of(task);
        taskJsonBody.setId(taskRepository.findOne(exampleTask).get().getId());
        return taskJsonBody;
    }

    public TaskJsonBody updateTaskByReporter(TaskJsonBody taskJsonBody) throws IllegalAccessException, EntityNotFoundException {

        Task updatedTask = convertToTask(taskJsonBody);

        //check user reporter
        Task oldTask = getTaskAndCheck(taskJsonBody.getId());

        //if current user isn't reporter of the task
        UserDetails userDetails = securityContextHolderFacade.getAuthenticateUser();
        String username = userDetails.getUsername();
        User reporter = userRepository.findByUsername(username).get();
        if (reporter.getId() != oldTask.getUserReporter().getId()) {
            throw new IllegalAccessException("Changing the task is allowed only for reporter");
        } else if (taskJsonBody.getReporter() != null && reporter.getId() != oldTask.getUserReporter().getId()) {
            throw new IllegalAccessException("Changing the task reporter isn't allowed");
        }

        //check user assignee
        Optional<User> userAssignee = userRepository.findById(taskJsonBody.getAssignee());
        if (userAssignee.isEmpty()) {
            throw new EntityNotFoundException("User with id = " + taskJsonBody.getAssignee() + " doesn't exist");
        } else {
            updatedTask.setUserAssignee(userAssignee.get());
            updatedTask.setUserReporter(reporter);
        }

        taskRepository.save(updatedTask);
        return convertToTaskJsonBody(taskRepository.findById(taskJsonBody.getId()).get());
    }

    public TaskJsonBody updateStatus(Long taskId, TaskStatus taskStatus) throws IllegalAccessException, EntityNotFoundException {

        //check if task exist
        Task task = getTaskAndCheck(taskId);
        task.setTaskStatus(taskStatus);


        //if current user isn't assignee of the task
        UserDetails userDetails = securityContextHolderFacade.getAuthenticateUser();
        String username = userDetails.getUsername();
        User assignee = userRepository.findByUsername(username).get();
        if (assignee.getId() != task.getUserAssignee().getId()) {
            throw new IllegalAccessException("Changing status is allowed only for assignee");
        }

        taskRepository.save(task);
        return convertToTaskJsonBody(taskRepository.findById(taskId).get());
    }

    public void deleteTask(Long id) throws IllegalAccessException, EntityNotFoundException {

        //check if task exist
        Task task = getTaskAndCheck(id);

        //if current user isn't reporter of the task
        UserDetails userDetails = securityContextHolderFacade.getAuthenticateUser();
        String username = userDetails.getUsername();
        User reporter = userRepository.findByUsername(username).get();
        if (reporter.getId() != task.getUserReporter().getId()) {
            throw new IllegalAccessException("Removal is permitted only to reporter");
        }

        taskRepository.deleteById(id);
    }

    protected Task convertToTask(TaskJsonBody taskJsonBody) {
        return Task.getBuilder()
                .setId(taskJsonBody.getId())
                .setTitle(taskJsonBody.getTitle())
                .setDescription(taskJsonBody.getDescription())
                .setTaskStatus(taskJsonBody.getStatus())
                .setTaskPriority(taskJsonBody.getPriority())
                .build();
    }

    protected TaskJsonBody convertToTaskJsonBody(Task task) {
        return TaskJsonBody.getBuilder()
                .setId(task.getId())
                .setTitle(task.getTitle())
                .setDescription(task.getDescription())
                .setStatus(task.getTaskStatus())
                .setPriority(task.getTaskPriority())
                .setAssignee(task.getUserAssignee().getId())
                .setReporter(task.getUserReporter().getId()).build();
    }

    protected Task getTaskAndCheck(Long id) throws EntityNotFoundException {
        Optional<Task> task = taskRepository.findById(id);
        if (!task.isPresent()) {
            throw new EntityNotFoundException("Task with id = " + id + " doesn't exist");
        }
        return task.get();
    }
}
