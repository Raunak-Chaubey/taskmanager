package com.raunak.taskmanager.dto;

import com.raunak.taskmanager.entity.TaskPriority;
import com.raunak.taskmanager.entity.TaskStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class TaskFilterCriteria {

    private TaskStatus status;
    private TaskPriority priority;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDateTo;

    private Boolean overdue;
    private String keyword;
}
