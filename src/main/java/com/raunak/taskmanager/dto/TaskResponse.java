package com.raunak.taskmanager.dto;

import com.raunak.taskmanager.entity.TaskPriority;
import com.raunak.taskmanager.entity.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private boolean overdue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String creatorUsername;
    private Set<LabelResponse> labels;
}
