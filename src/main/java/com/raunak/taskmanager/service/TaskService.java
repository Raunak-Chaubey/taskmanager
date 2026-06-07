package com.raunak.taskmanager.service;

import com.raunak.taskmanager.dto.TaskFilterCriteria;
import com.raunak.taskmanager.dto.TaskRequest;
import com.raunak.taskmanager.dto.TaskResponse;
import com.raunak.taskmanager.dto.TaskUpdateRequest;
import com.raunak.taskmanager.entity.Role;
import com.raunak.taskmanager.entity.Task;
import com.raunak.taskmanager.entity.TaskPriority;
import com.raunak.taskmanager.entity.TaskStatus;
import com.raunak.taskmanager.entity.User;
import com.raunak.taskmanager.exception.ForbiddenOperationException;
import com.raunak.taskmanager.exception.ResourceNotFoundException;
import com.raunak.taskmanager.mapper.TaskMapper;
import com.raunak.taskmanager.repository.TaskRepository;
import com.raunak.taskmanager.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CustomUserDetailsService userDetailsService;
    private final LabelService labelService;

    @Value("${app.pagination.default-size:10}")
    private int defaultPageSize;

    @Value("${app.pagination.max-size:50}")
    private int maxPageSize;

    @Transactional
    public TaskResponse createTask(String email, TaskRequest request) {
        User creator = userDetailsService.getUserByEmail(email);

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .overdue(isPastDue(request.getDueDate()))
                .creator(creator)
                .labels(labelService.resolveLabels(request.getLabelNames()))
                .build();

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasks(String email, TaskFilterCriteria filter, int page, int size, String sortBy, String direction) {
        User user = userDetailsService.getUserByEmail(email);
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Specification<Task> spec = buildSpecification(user, filter, false);
        return taskRepository.findAll(spec, pageable).map(TaskMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(TaskFilterCriteria filter, int page, int size, String sortBy, String direction) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Specification<Task> spec = buildSpecification(null, filter, true);
        return taskRepository.findAll(spec, pageable).map(TaskMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId, String email, Role role) {
        Task task = findTaskOrThrow(taskId);
        assertCanAccess(task, email, role);
        return TaskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, String email, Role role, TaskUpdateRequest request) {
        Task task = findTaskOrThrow(taskId);
        assertCanModify(task, email, role);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getLabelNames() != null) {
            task.getLabels().clear();
            task.getLabels().addAll(labelService.resolveLabels(request.getLabelNames()));
        }

        refreshOverdueFlag(task);

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long taskId, String email, Role role) {
        Task task = findTaskOrThrow(taskId);
        assertCanModify(task, email, role);
        taskRepository.delete(task);
    }

    private Task findTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    private void assertCanAccess(Task task, String email, Role role) {
        if (role == Role.ADMIN) {
            return;
        }
        if (!task.getCreator().getEmail().equals(email)) {
            throw new ForbiddenOperationException("You do not have access to this task");
        }
    }

    private void assertCanModify(Task task, String email, Role role) {
        assertCanAccess(task, email, role);
    }

    private Specification<Task> buildSpecification(User creator, TaskFilterCriteria filter, boolean adminScope) {
        List<Specification<Task>> specs = new ArrayList<>();

        if (!adminScope && creator != null) {
            specs.add(TaskSpecification.belongsToCreator(creator));
        }

        if (filter != null) {
            specs.add(TaskSpecification.hasStatus(filter.getStatus()));
            specs.add(TaskSpecification.hasPriority(filter.getPriority()));
            specs.add(TaskSpecification.dueDateFrom(filter.getDueDateFrom()));
            specs.add(TaskSpecification.dueDateTo(filter.getDueDateTo()));
            specs.add(TaskSpecification.isOverdue(filter.getOverdue()));
            specs.add(TaskSpecification.titleContains(filter.getKeyword()));
        }

        return specs.stream()
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());
    }

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        int resolvedSize = size <= 0 ? defaultPageSize : Math.min(size, maxPageSize);
        Sort sort = Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy == null || sortBy.isBlank() ? "createdAt" : sortBy);
        return PageRequest.of(Math.max(page, 0), resolvedSize, sort);
    }

    private boolean isPastDue(LocalDate dueDate) {
        return dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    private void refreshOverdueFlag(Task task) {
        if (task.getStatus() == TaskStatus.DONE) {
            task.setOverdue(false);
            return;
        }
        task.setOverdue(task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now()));
    }
}
