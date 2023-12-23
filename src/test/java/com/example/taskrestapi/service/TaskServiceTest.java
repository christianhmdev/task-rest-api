package com.example.taskrestapi.service;

import com.effectivemobile.taskmanager.enums.TaskPriority;
import com.effectivemobile.taskmanager.enums.TaskStatus;
import com.effectivemobile.taskmanager.model.Task;
import com.effectivemobile.taskmanager.model.User;
import com.effectivemobile.taskmanager.repository.TaskRepository;
import com.effectivemobile.taskmanager.repository.UserRepository;
import com.effectivemobile.taskmanager.security.SecurityContextHolderFacade;
import com.effectivemobile.taskmanager.security.UserDetailsImpl;
import com.effectivemobile.taskmanager.transportObject.TaskJsonBody;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class TaskServiceTest {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private TaskService taskService;
    private SecurityContextHolderFacade securityContextHolderFacade;


    @Before
    public void init() {
        taskRepository = Mockito.mock(TaskRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        securityContextHolderFacade = Mockito.mock(SecurityContextHolderFacade.class);
        taskService = new TaskService(taskRepository, userRepository, securityContextHolderFacade);
    }

    @Test
    public void should_createTaskWithReporterIsCurrentUser_whenReporterIsNotSpecified() {

        User user = new User(1L, "TestName", "test@gmail.com", "123");
        UserDetails userDetails = new UserDetailsImpl(1L, "TestName", "Test@gmail.com", "123");

        TaskJsonBody inputTask = TaskJsonBody.getBuilder()
                .setTitle("title")
                .setDescription("description")
                .setPriority(TaskPriority.HIGH)
                .setStatus(TaskStatus.IN_PROGRESS)
                .setAssignee(user.getId())
                .setReporter(user.getId())
                .build();

        TaskJsonBody outputTask = TaskJsonBody.getBuilder()
                .setId(1L)
                .setTitle("title")
                .setDescription("description")
                .setPriority(TaskPriority.HIGH)
                .setStatus(TaskStatus.IN_PROGRESS)
                .setAssignee(user.getId())
                .setReporter(user.getId())
                .build();

        Task createdTask = Task.getBuilder()
                .setId(1L)
                .setTitle("title")
                .setDescription("description")
                .setTaskPriority(TaskPriority.HIGH)
                .setTaskStatus(TaskStatus.IN_PROGRESS)
                .setUserAssignee(user)
                .setUserReporter(user)
                .build();


        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findOne(Mockito.any())).thenReturn(Optional.ofNullable(createdTask));
        Mockito.when(securityContextHolderFacade.getAuthenticateUser()).thenReturn(userDetails);

        Assert.assertTrue(outputTask.equals(taskService.createTask(inputTask)));
    }

    @Test
    public void should_updateTask_whenCurrentUserIsReporterOfTheTask() throws IllegalAccessException {
        User user = new User(1L, "TestName", "test@gmail.com", "123");
        UserDetails userDetails = new UserDetailsImpl(1L, "TestName", "Test@gmail.com", "123");

        TaskJsonBody inputTask = TaskJsonBody.getBuilder()
                .setTitle("title")
                .setDescription("description")
                .setPriority(TaskPriority.HIGH)
                .setStatus(TaskStatus.IN_PROGRESS)
                .setAssignee(user.getId())
                .setReporter(user.getId())
                .build();

        TaskJsonBody outputTask = TaskJsonBody.getBuilder()
                .setId(1L)
                .setTitle("Updated Title")
                .setDescription("description has been updated")
                .setPriority(TaskPriority.HIGH)
                .setStatus(TaskStatus.IN_PROGRESS)
                .setAssignee(user.getId())
                .setReporter(user.getId())
                .build();

        Task updatedTask = Task.getBuilder()
                .setId(1L)
                .setTitle("Updated Title")
                .setDescription("description has been updated")
                .setTaskPriority(TaskPriority.HIGH)
                .setTaskStatus(TaskStatus.IN_PROGRESS)
                .setUserAssignee(user)
                .setUserReporter(user)
                .build();


        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(updatedTask));
        Mockito.when(securityContextHolderFacade.getAuthenticateUser()).thenReturn(userDetails);

        Assert.assertTrue(outputTask.equals(taskService.updateTaskByReporter(inputTask)));
    }

    @Test
    public void should_updateStatusOfTask_whenCurrentUserIsAssigneeOfTheTask() throws IllegalAccessException {
        User user = new User(1L, "TestName", "test@gmail.com", "123");
        UserDetails userDetails = new UserDetailsImpl(1L, "TestName", "Test@gmail.com", "123");

        TaskJsonBody inputTask = TaskJsonBody.getBuilder()
                .setTitle("title")
                .setDescription("description")
                .setPriority(TaskPriority.HIGH)
                .setStatus(TaskStatus.BACKLOG)
                .setAssignee(user.getId())
                .setReporter(user.getId())
                .build();

        TaskJsonBody outputTask = TaskJsonBody.getBuilder()
                .setId(1L)
                .setTitle("title")
                .setDescription("description")
                .setPriority(TaskPriority.HIGH)
                .setStatus(TaskStatus.IN_PROGRESS)
                .setAssignee(user.getId())
                .setReporter(user.getId())
                .build();

        Task createdTask = Task.getBuilder()
                .setId(1L)
                .setTitle("title")
                .setDescription("description")
                .setTaskPriority(TaskPriority.HIGH)
                .setTaskStatus(TaskStatus.IN_PROGRESS)
                .setUserAssignee(user)
                .setUserReporter(user)
                .build();


        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findById(1L)).thenReturn(Optional.of(createdTask));
        Mockito.when(securityContextHolderFacade.getAuthenticateUser()).thenReturn(userDetails);

        Assert.assertTrue(outputTask.equals(taskService.updateStatus(1L, TaskStatus.IN_PROGRESS)));
    }

    @Test
    public void should_deleteTask_whenCurrentUserIsReporterOfTheTask() throws IllegalAccessException {
        User user = new User(1L, "TestName", "test@gmail.com", "123");
        UserDetails userDetails = new UserDetailsImpl(1L, "TestName", "Test@gmail.com", "123");

        Task task = Task.getBuilder()
                .setId(2L)
                .setTitle("title")
                .setDescription("description")
                .setTaskPriority(TaskPriority.HIGH)
                .setTaskStatus(TaskStatus.IN_PROGRESS)
                .setUserAssignee(user)
                .setUserReporter(user)
                .build();


        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(taskRepository.findById(2L)).thenReturn(Optional.of(task));
        Mockito.when(securityContextHolderFacade.getAuthenticateUser()).thenReturn(userDetails);

        taskService.deleteTask(2L);
        Mockito.verify(taskRepository, Mockito.atLeastOnce()).deleteById(2L);
    }
}
