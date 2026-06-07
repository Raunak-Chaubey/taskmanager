package com.raunak.taskmanager.controller;

import com.raunak.taskmanager.dto.ApiResponse;
import com.raunak.taskmanager.dto.TaskFilterCriteria;
import com.raunak.taskmanager.dto.TaskResponse;
import com.raunak.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaskController {

    private final TaskService taskService;

    @GetMapping
    public ApiResponse<Page<TaskResponse>> getAllTasks(
            TaskFilterCriteria filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return ApiResponse.ok(taskService.getAllTasks(filter, page, size, sortBy, direction));
    }
}
