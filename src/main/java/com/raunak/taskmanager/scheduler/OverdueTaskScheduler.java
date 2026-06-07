package com.raunak.taskmanager.scheduler;

import com.raunak.taskmanager.entity.Task;
import com.raunak.taskmanager.entity.TaskStatus;
import com.raunak.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OverdueTaskScheduler {

    private final TaskRepository taskRepository;

    @Scheduled(cron = "${app.scheduler.overdue-tasks-cron:0 0 6 * * *}")
    @Transactional
    public void markOverdueTasks() {
        LocalDate today = LocalDate.now();
        List<Task> tasksToMark = taskRepository.findTasksToMarkOverdue(today, TaskStatus.DONE);

        if (tasksToMark.isEmpty()) {
            log.info("Overdue task scan complete: no tasks to update");
            return;
        }

        int updatedCount = taskRepository.markOverdueTasks(today, TaskStatus.DONE);

        tasksToMark.forEach(task ->
                log.warn(
                        "OVERDUE TASK | id={} | title='{}' | creator='{}' | dueDate={}",
                        task.getId(),
                        task.getTitle(),
                        task.getCreator().getUsername(),
                        task.getDueDate()
                )
        );

        log.info("Overdue task scan complete: {} task(s) marked overdue", updatedCount);
    }
}
