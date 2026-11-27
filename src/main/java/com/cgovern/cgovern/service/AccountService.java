package com.cgovern.cgovern.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cgovern.cgovern.domain.UserAccount;
import com.cgovern.cgovern.repo.UserAccountRepository;

@Service
public class AccountService {

    private final UserAccountRepository users;
    private final PasswordEncoder encoder;

    public AccountService(UserAccountRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional
    public UserAccount register(String fullName, String email, String rawPassword) {
        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("An account already exists for that email.");
        }
        UserAccount account = new UserAccount();
        account.setFullName(fullName);
        account.setEmail(email.trim().toLowerCase());
        account.setPasswordHash(encoder.encode(rawPassword));
        account.setRole("ROLE_USER");
        return users.save(account);
    }
}
