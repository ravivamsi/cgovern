package com.cgovern.cgovern.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgovern.cgovern.domain.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
}
