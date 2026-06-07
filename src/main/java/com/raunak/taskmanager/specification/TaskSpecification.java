package com.raunak.taskmanager.specification;

import com.raunak.taskmanager.entity.Task;
import com.raunak.taskmanager.entity.TaskPriority;
import com.raunak.taskmanager.entity.TaskStatus;
import com.raunak.taskmanager.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<Task> belongsToCreator(User creator) {
        return (root, query, cb) -> cb.equal(root.get("creator"), creator);
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, cb) ->
                priority == null ? cb.conjunction() : cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> dueDateFrom(LocalDate from) {
        return (root, query, cb) ->
                from == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("dueDate"), from);
    }

    public static Specification<Task> dueDateTo(LocalDate to) {
        return (root, query, cb) ->
                to == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("dueDate"), to);
    }

    public static Specification<Task> isOverdue(Boolean overdue) {
        return (root, query, cb) ->
                overdue == null ? cb.conjunction() : cb.equal(root.get("overdue"), overdue);
    }

    public static Specification<Task> titleContains(String keyword) {
        return (root, query, cb) ->
                keyword == null || keyword.isBlank()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }
}
