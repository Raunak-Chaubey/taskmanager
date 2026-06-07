package com.raunak.taskmanager.controller;

import com.raunak.taskmanager.dto.ApiResponse;
import com.raunak.taskmanager.dto.TaskFilterCriteria;
import com.raunak.taskmanager.dto.TaskRequest;
import com.raunak.taskmanager.dto.TaskResponse;
import com.raunak.taskmanager.dto.TaskUpdateRequest;
import com.raunak.taskmanager.entity.Role;
import com.raunak.taskmanager.entity.User;
import com.raunak.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(taskService.createTask(user.getEmail(), request));
    }

    @GetMapping
    public ApiResponse<Page<TaskResponse>> getTasks(
            TaskFilterCriteria filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(taskService.getTasks(user.getEmail(), filter, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskResponse> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(taskService.getTaskById(id, user.getEmail(), user.getRole()));
    }

    @PutMapping("/{id}")
    public ApiResponse<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(taskService.updateTask(id, user.getEmail(), user.getRole(), request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        taskService.deleteTask(id, user.getEmail(), user.getRole());
        return ApiResponse.okMessage("Task deleted successfully");
    }
}
