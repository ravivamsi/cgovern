package com.cgovern.cgovern.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgovern.cgovern.domain.JobRun;

public interface JobRunRepository extends JpaRepository<JobRun, Long> {
    List<JobRun> findByJobIdOrderByStartedAtDesc(Long jobId);
}
