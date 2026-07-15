package com.maksymsuvorov.taskflow.repository.specification;

import com.maksymsuvorov.taskflow.model.Project;
import com.maksymsuvorov.taskflow.model.Task;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public final class ProjectSpecification {

    private ProjectSpecification() {
    }

    public static Specification<Project> visibleTo(String email) {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Task> task = subquery.from(Task.class);
            subquery.select(cb.literal(1L)).where(
                    cb.equal(task.get("project"), root),
                    cb.equal(task.join("assignee", JoinType.LEFT).get("email"), email)
            );

            return cb.or(
                    cb.equal(root.get("owner").get("email"), email),
                    cb.exists(subquery)
            );
        };
    }

    public static Specification<Project> nameContains(String name) {
        return (root, query, cb) -> name == null || name.isBlank()
                ? null
                : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Project> hasOwner(Long ownerId) {
        return (root, query, cb) -> ownerId == null ? null : cb.equal(root.get("owner").get("id"), ownerId);
    }

}
