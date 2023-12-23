package com.example.taskrestapi.enums;

public enum TaskStatus {
    BACKLOG, IN_PROGRESS, CLOSED;

    public String toString(TaskStatus taskStatus) {
        return taskStatus.name();
    }
}
