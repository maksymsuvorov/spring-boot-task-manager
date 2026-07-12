package com.maksymsuvorov.taskflow.service;

import com.maksymsuvorov.taskflow.controller.dto.TaskCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.TaskUpdateRequest;
import com.maksymsuvorov.taskflow.model.Task;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface TaskServiceInterface {
    List<Task> getTasksByProjectId(Long projectId) throws EntityNotFoundException;

    Task getTaskById(Long taskId) throws EntityNotFoundException;

    Task createTask(Long projectId, TaskCreateRequest request) throws EntityNotFoundException;

    Task updateTaskById(Long taskId, TaskUpdateRequest request) throws EntityNotFoundException;

    void deleteTaskById(Long taskId) throws EntityNotFoundException;
}
