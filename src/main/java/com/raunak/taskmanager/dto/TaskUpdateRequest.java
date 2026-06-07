package com.raunak.taskmanager.dto;

import com.raunak.taskmanager.entity.TaskPriority;
import com.raunak.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class TaskUpdateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;

    private Set<@NotBlank @Size(max = 50) String> labelNames;
}
