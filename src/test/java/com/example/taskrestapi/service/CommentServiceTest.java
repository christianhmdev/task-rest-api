package com.example.taskrestapi.service;

import com.effectivemobile.taskmanager.enums.TaskPriority;
import com.effectivemobile.taskmanager.enums.TaskStatus;
import com.effectivemobile.taskmanager.model.Comment;
import com.effectivemobile.taskmanager.model.Task;
import com.effectivemobile.taskmanager.model.User;
import com.effectivemobile.taskmanager.repository.CommentRepository;
import com.effectivemobile.taskmanager.repository.TaskRepository;
import com.effectivemobile.taskmanager.repository.UserRepository;
import com.effectivemobile.taskmanager.security.SecurityContextHolderFacade;
import com.effectivemobile.taskmanager.security.UserDetailsImpl;
import com.effectivemobile.taskmanager.transportObject.CommentJsonBody;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Optional;

public class CommentServiceTest {

    private CommentRepository commentRepository;
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private CommentService commentService;
    private SecurityContextHolderFacade securityContextHolderFacade;


    @Before
    public void init() {
        commentRepository = Mockito.mock(CommentRepository.class);
        taskRepository = Mockito.mock(TaskRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        securityContextHolderFacade = Mockito.mock(SecurityContextHolderFacade.class);
        commentService = new CommentService(commentRepository, userRepository, taskRepository, securityContextHolderFacade);
    }

    @Test
    public void should_createComment_whenTaskExist() {
        User user = new User(1L, "TestName", "test@gmail.com", "123");
        UserDetails userDetails = new UserDetailsImpl(1L, "TestName", "Test@gmail.com", "123");

        Timestamp time = new Timestamp(System.currentTimeMillis());
        CommentJsonBody outputComment = CommentJsonBody.getBuilder()
                .setId(1L)
                .setContent("Test Content")
                .setCreationTime(time)
                .setTask(1L)
                .setUser(1L)
                .build();

        Task task = Task.getBuilder()
                .setId(1L)
                .setTitle("title")
                .setDescription("description")
                .setTaskPriority(TaskPriority.HIGH)
                .setTaskStatus(TaskStatus.IN_PROGRESS)
                .setUserAssignee(user)
                .setUserReporter(user)
                .build();

        CommentJsonBody inputComment = CommentJsonBody.getBuilder()
                .setTask(task.getId())
                .setContent("Test Content")
                .build();


        Comment createdComment = new Comment(1L, "Test Content", time, task, user);

        Mockito.when(securityContextHolderFacade.getAuthenticateUser()).thenReturn(userDetails);
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(commentRepository.findOne(Mockito.any())).thenReturn(Optional.of(createdComment));
        Mockito.when(taskRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(task));

        Assert.assertTrue(outputComment.equals(commentService.createComment(inputComment)));
    }
}
