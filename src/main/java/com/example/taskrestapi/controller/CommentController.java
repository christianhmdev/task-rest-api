package com.example.taskrestapi.controller;

import com.example.taskrestapi.service.CommentService;
import com.example.taskrestapi.transportObject.CommentJsonBody;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/taskmanager/comment")
public class CommentController {

    @Autowired
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Create a new comment", description = "Create a new comment")
    @PostMapping("/add")
    public CommentJsonBody createComment(@RequestBody CommentJsonBody commentJsonBody) {
        try {
            return commentService.createComment(commentJsonBody);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Get comments associated with a task by its ID", description = "Get comments associated with a task by its ID")
    @GetMapping("/getComments")
    public List<CommentJsonBody> getCommentsByTaskId(@RequestParam Long id) {
        return commentService.getCommentsByTaskId(id);
    }
}
