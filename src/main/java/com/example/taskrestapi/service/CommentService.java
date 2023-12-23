package com.example.taskrestapi.service;

import com.example.taskrestapi.model.Comment;
import com.example.taskrestapi.model.Task;
import com.example.taskrestapi.model.User;
import com.example.taskrestapi.repository.CommentRepository;
import com.example.taskrestapi.repository.TaskRepository;
import com.example.taskrestapi.repository.UserRepository;
import com.example.taskrestapi.security.SecurityContextHolderFacade;
import com.example.taskrestapi.transportObject.CommentJsonBody;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private SecurityContextHolderFacade securityContextHolderFacade;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, TaskRepository taskRepository, SecurityContextHolderFacade securityContextHolderFacade) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.securityContextHolderFacade = securityContextHolderFacade;
    }

    public CommentJsonBody createComment(CommentJsonBody commentJsonBody) throws EntityNotFoundException {

        Comment comment = new Comment();

        //find reporter user
        UserDetails userDetails = securityContextHolderFacade.getAuthenticateUser();
        String username = userDetails.getUsername();
        User reporter = userRepository.findByUsername(username).get();
        comment.setUser(reporter);

        //set creation time
        comment.setCreationTime(new Timestamp(System.currentTimeMillis()));

        //find task
        Optional<Task> task = taskRepository.findById(commentJsonBody.getTask());
        if (task.isEmpty()) {
            throw new EntityNotFoundException("Task with id = " + commentJsonBody.getTask() + " doesn't exist");
        } else comment.setTask(task.get());

        comment.setContent(commentJsonBody.getContent());


        commentRepository.save(comment);

        Example<Comment> example = Example.of(comment);
        return convertToCommentJsonBody(commentRepository.findOne(example).get());
    }

    public List<CommentJsonBody> getCommentsByTaskId(Long id) {
        List<Comment> comments = commentRepository.getCommentsByTaskId(id);
        return comments.stream().map(c -> convertToCommentJsonBody(c)).toList();
    }

    protected CommentJsonBody convertToCommentJsonBody(Comment comment) {
        return CommentJsonBody.getBuilder().setId(comment.getId())
                .setContent(comment.getContent())
                .setCreationTime(comment.getCreationTime())
                .setTask(comment.getTask().getId())
                .setUser(comment.getUser().getId())
                .build();
    }
}
