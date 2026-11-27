package com.cgovern.cgovern.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgovern.cgovern.domain.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByJobIdOrderByParallelGroupAscOrderIndexAsc(Long jobId);
}
