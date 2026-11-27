package com.cgovern.cgovern.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgovern.cgovern.domain.FeatureModule;
import com.cgovern.cgovern.domain.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    List<Organization> findByModuleOrderByNameAsc(FeatureModule module);
}
