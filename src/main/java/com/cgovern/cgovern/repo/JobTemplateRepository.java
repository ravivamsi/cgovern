package com.cgovern.cgovern.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgovern.cgovern.domain.JobTemplate;

public interface JobTemplateRepository extends JpaRepository<JobTemplate, Long> {
}
