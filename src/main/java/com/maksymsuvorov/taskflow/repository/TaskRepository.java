package com.maksymsuvorov.taskflow.repository;

import com.maksymsuvorov.taskflow.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByProjectId(Long projectId);

    boolean existsByAssigneeId(Long assigneeId);

    List<Task> findAllByProjectId(Long projectId);

}
