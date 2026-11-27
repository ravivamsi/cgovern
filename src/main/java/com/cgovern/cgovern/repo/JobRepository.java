package com.cgovern.cgovern.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgovern.cgovern.domain.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByServiceIdOrderByOrderIndexAsc(Long serviceId);
}
