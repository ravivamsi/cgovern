package com.cgovern.cgovern.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgovern.cgovern.domain.AppService;

public interface AppServiceRepository extends JpaRepository<AppService, Long> {
    List<AppService> findByOrganizationIdOrderByNameAsc(Long organizationId);
}
