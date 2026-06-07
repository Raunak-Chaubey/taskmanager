package com.raunak.taskmanager.repository;

import com.raunak.taskmanager.entity.Task;
import com.raunak.taskmanager.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query("""
            SELECT t FROM Task t JOIN FETCH t.creator
            WHERE t.dueDate < :today
              AND t.status <> :doneStatus
              AND t.overdue = false
            """)
    List<Task> findTasksToMarkOverdue(
            @Param("today") LocalDate today,
            @Param("doneStatus") TaskStatus doneStatus
    );

    @Modifying
    @Query("""
            UPDATE Task t
            SET t.overdue = true, t.updatedAt = CURRENT_TIMESTAMP
            WHERE t.dueDate < :today
              AND t.status <> :doneStatus
              AND t.overdue = false
            """)
    int markOverdueTasks(
            @Param("today") LocalDate today,
            @Param("doneStatus") TaskStatus doneStatus
    );
}
