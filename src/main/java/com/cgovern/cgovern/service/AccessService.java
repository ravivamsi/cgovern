package com.cgovern.cgovern.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cgovern.cgovern.domain.OrgMembership;
import com.cgovern.cgovern.domain.OrgRole;
import com.cgovern.cgovern.domain.Organization;
import com.cgovern.cgovern.domain.UserAccount;
import com.cgovern.cgovern.repo.OrgMembershipRepository;
import com.cgovern.cgovern.repo.OrganizationRepository;
import com.cgovern.cgovern.repo.UserAccountRepository;

@Service
public class AccessService {

    private final UserAccountRepository users;
    private final OrganizationRepository orgs;
    private final OrgMembershipRepository memberships;

    public AccessService(UserAccountRepository users, OrganizationRepository orgs,
            OrgMembershipRepository memberships) {
        this.users = users;
        this.orgs = orgs;
        this.memberships = memberships;
    }

    public List<UserAccount> users() {
        return users.findAll();
    }

    public List<Organization> organizations() {
        return orgs.findAll();
    }

    public Organization organization(Long id) {
        return orgs.findById(id).orElseThrow();
    }

    public List<OrgMembership> membershipsForOrg(Long orgId) {
        return memberships.findByOrganizationIdOrderByIdAsc(orgId);
    }

    public List<OrgMembership> membershipsForUser(Long userId) {
        return memberships.findByUserIdOrderByIdAsc(userId);
    }

    @Transactional
    public UserAccount setPlatformRole(Long userId, String role) {
        if (!"ROLE_ADMIN".equals(role) && !"ROLE_USER".equals(role)) {
            throw new IllegalArgumentException("Role must be ROLE_ADMIN or ROLE_USER");
        }
        UserAccount user = users.findById(userId).orElseThrow();
        user.setRole(role);
        return users.save(user);
    }

    @Transactional
    public OrgMembership grant(Long orgId, String email, OrgRole role) {
        UserAccount user = users.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("No user registered with that email."));
        Organization organization = orgs.findById(orgId).orElseThrow();
        OrgMembership membership = memberships.findByUserIdAndOrganizationId(user.getId(), orgId)
                .orElseGet(OrgMembership::new);
        membership.setUser(user);
        membership.setOrganization(organization);
        membership.setRole(role);
        return memberships.save(membership);
    }

    @Transactional
    public OrgMembership changeOrgRole(Long membershipId, OrgRole role) {
        OrgMembership membership = memberships.findById(membershipId).orElseThrow();
        membership.setRole(role);
        return memberships.save(membership);
    }

    @Transactional
    public void revoke(Long membershipId) {
        memberships.deleteById(membershipId);
    }
}
