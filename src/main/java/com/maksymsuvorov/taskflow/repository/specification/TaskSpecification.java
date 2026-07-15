package com.maksymsuvorov.taskflow.repository.specification;

import com.maksymsuvorov.taskflow.model.Task;
import com.maksymsuvorov.taskflow.model.TaskPriority;
import com.maksymsuvorov.taskflow.model.TaskStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<Task> visibleTo(String email) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.join("assignee", JoinType.LEFT).get("email"), email),
                cb.equal(root.get("project").get("owner").get("email"), email)
        );
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, cb) -> priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> hasProject(Long projectId) {
        return (root, query, cb) -> projectId == null ? null : cb.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Task> hasAssignee(Long assigneeId) {
        return (root, query, cb) -> assigneeId == null ? null : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    public static Specification<Task> dueBefore(LocalDate date) {
        return (root, query, cb) -> date == null ? null : cb.lessThan(root.get("dueDate"), date);
    }

}
