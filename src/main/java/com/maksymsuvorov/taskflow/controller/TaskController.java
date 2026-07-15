package com.maksymsuvorov.taskflow.controller;

import com.maksymsuvorov.taskflow.controller.dto.PageResponse;
import com.maksymsuvorov.taskflow.controller.dto.TaskCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.TaskResponse;
import com.maksymsuvorov.taskflow.controller.dto.TaskUpdateRequest;
import com.maksymsuvorov.taskflow.controller.dto.filter.TaskFilter;
import com.maksymsuvorov.taskflow.mapper.TaskMapper;
import com.maksymsuvorov.taskflow.model.Task;
import com.maksymsuvorov.taskflow.service.TaskServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskServiceInterface taskService;

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long projectId,
                                                   @Valid @RequestBody TaskCreateRequest request) {
        Task task = this.taskService.createTask(projectId, request);

        return ResponseEntity
                .created(URI.create("/api/tasks/" + task.getId()))
                .body(TaskMapper.toResponse(task));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskResponse> getTasksByProjectId(@PathVariable Long projectId) {
        return this.taskService.getTasksByProjectId(projectId).stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @GetMapping("/tasks")
    public PageResponse<TaskResponse> getTasks(@ModelAttribute TaskFilter filter,
                                               @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return PageResponse.from(
                this.taskService.getTasks(filter, pageable)
                        .map(TaskMapper::toResponse)
        );
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse getTaskById(@PathVariable Long id) {
        return TaskMapper.toResponse(this.taskService.getTaskById(id));
    }

    @PutMapping("/tasks/{id}")
    public TaskResponse updateTaskById(@PathVariable Long id,
                                       @Valid @RequestBody TaskUpdateRequest request) {
        return TaskMapper.toResponse(this.taskService.updateTaskById(id, request));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        this.taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

}
