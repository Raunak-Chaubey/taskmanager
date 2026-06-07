package com.raunak.taskmanager.mapper;

import com.raunak.taskmanager.dto.LabelResponse;
import com.raunak.taskmanager.dto.TaskResponse;
import com.raunak.taskmanager.entity.Label;
import com.raunak.taskmanager.entity.Task;

import java.util.Set;
import java.util.stream.Collectors;

public final class TaskMapper {

    private TaskMapper() {
    }

    public static TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .overdue(task.isOverdue())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .creatorUsername(task.getCreator().getUsername())
                .labels(toLabelResponses(task.getLabels()))
                .build();
    }

    private static Set<LabelResponse> toLabelResponses(Set<Label> labels) {
        return labels.stream()
                .map(label -> LabelResponse.builder()
                        .id(label.getId())
                        .name(label.getName())
                        .color(label.getColor())
                        .build())
                .collect(Collectors.toSet());
    }
}
