package com.cgovern.cgovern.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cgovern.cgovern.domain.UserAccount;
import com.cgovern.cgovern.repo.UserAccountRepository;

@Service
public class CgovernUserDetailsService implements UserDetailsService {

    private final UserAccountRepository users;

    public CgovernUserDetailsService(UserAccountRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = users.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(account.getEmail(), account.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(account.getRole())));
    }
}
