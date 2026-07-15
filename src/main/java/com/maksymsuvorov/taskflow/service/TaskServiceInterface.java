package com.maksymsuvorov.taskflow.service;

import com.maksymsuvorov.taskflow.controller.dto.TaskCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.TaskUpdateRequest;
import com.maksymsuvorov.taskflow.controller.dto.filter.TaskFilter;
import com.maksymsuvorov.taskflow.model.Task;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface TaskServiceInterface {
    Page<Task> getTasks(TaskFilter filter, Pageable pageable);

    List<Task> getTasksByProjectId(Long projectId) throws EntityNotFoundException;

    Task getTaskById(Long taskId) throws EntityNotFoundException;

    Task createTask(Long projectId, TaskCreateRequest request) throws EntityNotFoundException;

    Task updateTaskById(Long taskId, TaskUpdateRequest request) throws EntityNotFoundException;

    void deleteTaskById(Long taskId) throws EntityNotFoundException;
}
