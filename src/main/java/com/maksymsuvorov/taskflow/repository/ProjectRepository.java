package com.maksymsuvorov.taskflow.repository;

import com.maksymsuvorov.taskflow.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByOwnerId(Long ownerId);

}
