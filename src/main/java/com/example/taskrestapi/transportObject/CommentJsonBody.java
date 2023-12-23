package com.example.taskrestapi.transportObject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
public class CommentJsonBody {

    private Long id;

    @NotBlank(message = "Title must be specified")
    @Size(max = 100, message = "Content should not be greater than 100")
    private String content;

    private Timestamp creationTime;

    @NotNull(message = "Task cannot be null")
    private Long task;

    private Long user;

    public CommentJsonBody(Builder builder) {
        this.id = builder.getId();
        this.content = builder.getContent();
        this.creationTime = builder.getCreationTime();
        this.task = builder.getTask();
        this.user = builder.getUser();
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentJsonBody that = (CommentJsonBody) o;
        return Objects.equals(id, that.id) && Objects.equals(content, that.content) && Objects.equals(creationTime, that.creationTime) && Objects.equals(task, that.task) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, creationTime, task, user);
    }

    @Getter
    public static class Builder {

        private Long id;
        private String content;
        private Timestamp creationTime;
        private Long task;
        private Long user;

        public Builder() {
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setCreationTime(Timestamp creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public Builder setTask(Long task) {
            this.task = task;
            return this;
        }

        public Builder setUser(Long user) {
            this.user = user;
            return this;
        }

        public CommentJsonBody build() {
            return new CommentJsonBody(this);
        }
    }
}
