package com.example.taskrestapi.controller;

import com.example.taskrestapi.repository.CommentRepository;
import com.example.taskrestapi.repository.TaskRepository;
import com.example.taskrestapi.repository.UserRepository;
import com.example.taskrestapi.security.SecurityContextHolderFacade;
import com.example.taskrestapi.service.CommentService;
import com.example.taskrestapi.service.TaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ControllerConfig {

    @Bean
    public TaskService taskService(TaskRepository taskRepository, UserRepository userRepository, SecurityContextHolderFacade securityContextHolderFacade) {
        return new TaskService(taskRepository, userRepository, securityContextHolderFacade);
    }

    @Bean
    public CommentService commentService(CommentRepository commentRepository, UserRepository userRepository, TaskRepository taskRepository, SecurityContextHolderFacade securityContextHolderFacade) {
        return new CommentService(commentRepository, userRepository, taskRepository, securityContextHolderFacade);
    }
}
