package com.maksymsuvorov.taskflow.service;

import com.maksymsuvorov.taskflow.controller.dto.TaskCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.TaskUpdateRequest;
import com.maksymsuvorov.taskflow.controller.dto.filter.TaskFilter;
import com.maksymsuvorov.taskflow.model.Project;
import com.maksymsuvorov.taskflow.model.Task;
import com.maksymsuvorov.taskflow.model.User;
import com.maksymsuvorov.taskflow.repository.ProjectRepository;
import com.maksymsuvorov.taskflow.repository.TaskRepository;
import com.maksymsuvorov.taskflow.repository.UserRepository;
import com.maksymsuvorov.taskflow.repository.specification.TaskSpecification;
import com.maksymsuvorov.taskflow.security.AuthorizationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImplementation implements TaskServiceInterface {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    private static final Set<String> ALLOWED_SORT_PROPERTIES =
            Set.of("title", "status", "priority", "dueDate", "createdAt", "updatedAt");

    @Override
    @Transactional(readOnly = true)
    public Page<Task> getTasks(TaskFilter filter, Pageable pageable) {
        SortValidator.validate(pageable.getSort(), ALLOWED_SORT_PROPERTIES);

        Specification<Task> specification = Specification.allOf(
                this.authorizationService.isAdmin()
                        ? Specification.unrestricted()
                        : TaskSpecification.visibleTo(this.authorizationService.currentEmail()),
                TaskSpecification.hasStatus(filter.status()),
                TaskSpecification.hasPriority(filter.priority()),
                TaskSpecification.hasProject(filter.projectId()),
                TaskSpecification.hasAssignee(filter.assigneeId()),
                TaskSpecification.dueBefore(filter.dueBefore())
        );

        return this.taskRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByProjectId(Long projectId) throws EntityNotFoundException {
        if (!this.projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project with id " + projectId + " was not found.");
        }

        return this.taskRepository.findAllByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public Task getTaskById(Long taskId) throws EntityNotFoundException {
        return this.taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " was not found."));
    }

    @Override
    public Task createTask(Long projectId, TaskCreateRequest request) throws EntityNotFoundException {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " does not exist."));

        // Only the project owner (or an admin) can add tasks to a project.
        this.authorizationService.requireProjectOwnerOrAdmin(project);

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setProject(project);
        task.setAssignee(this.resolveAssignee(request.assigneeId()));

        return this.taskRepository.save(task);
    }

    @Override
    public Task updateTaskById(Long taskId, TaskUpdateRequest request) throws EntityNotFoundException {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " was not found."));

        // Project owner, current assignee, or admin.
        this.authorizationService.requireTaskModifyAccess(task);

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setAssignee(this.resolveAssignee(request.assigneeId()));

        return task;
    }

    @Override
    public void deleteTaskById(Long taskId) throws EntityNotFoundException {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " was not found."));

        // Deleting is owner-level: assignees can update, not delete.
        this.authorizationService.requireProjectOwnerOrAdmin(task.getProject());

        this.taskRepository.delete(task);
    }

    private User resolveAssignee(Long assigneeId) throws EntityNotFoundException {
        if (assigneeId == null) {
            return null;
        }

        return this.userRepository.findById(assigneeId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + assigneeId + " does not exist."));
    }

}
