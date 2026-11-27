package com.cgovern.cgovern.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgovern.cgovern.domain.OrgMembership;

public interface OrgMembershipRepository extends JpaRepository<OrgMembership, Long> {
    List<OrgMembership> findByOrganizationIdOrderByIdAsc(Long organizationId);

    List<OrgMembership> findByUserIdOrderByIdAsc(Long userId);

    Optional<OrgMembership> findByUserIdAndOrganizationId(Long userId, Long organizationId);

    boolean existsByUserIdAndOrganizationId(Long userId, Long organizationId);
}
