package com.example.taskrestapi.model;

import com.example.taskrestapi.enums.TaskPriority;
import com.example.taskrestapi.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@RequiredArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", columnDefinition = "VARCHAR(40)", nullable = false)
    @NotBlank(message = "Title must be specified")
    @Size(max = 40, message = "Title should not be greater than 40")
    private String title;

    @Column(name = "description", columnDefinition = "VARCHAR(100)", nullable = false)
    @NotBlank(message = "Description must be specified")
    @Size(max = 100, message = "Description should not be greater than 100")
    private String description;

    @Column(name = "status", columnDefinition = "VARCHAR(40) default 'BACKLOG'", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "taskStatus must be specified")
    private TaskStatus taskStatus;

    @Column(name = "priority", columnDefinition = "VARCHAR(40)", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "taskPriority must be specified")
    private TaskPriority taskPriority;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "reporter_id")
    private User userReporter;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "assignee_id")
    private User userAssignee;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Task(Builder builder) {
        this.id = builder.getId();
        this.title = builder.getTitle();
        this.description = builder.getDescription();
        this.taskStatus = builder.getTaskStatus();
        this.taskPriority = builder.getTaskPriority();
        this.userReporter = builder.getUserReporter();
        this.userAssignee = builder.getUserAssignee();
        this.comments = builder.getComments();
    }

    public static Builder getBuilder() {
        return new Builder();
    }


    @Getter
    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private TaskStatus taskStatus;
        private TaskPriority taskPriority;
        private User userReporter;
        private User userAssignee;
        private List<Comment> comments;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setTaskStatus(TaskStatus status) {
            this.taskStatus = status;
            return this;
        }

        public Builder setTaskPriority(TaskPriority priority) {
            this.taskPriority = priority;
            return this;
        }

        public Builder setUserReporter(User reporter) {
            this.userReporter = reporter;
            return this;
        }

        public Builder setUserAssignee(User assignee) {
            this.userAssignee = assignee;
            return this;
        }

        public Builder setComments(List<Comment> comments) {
            this.comments = comments;
            return this;
        }

        public Task build() {
            return new Task(this);
        }
    }
}
