package com.example.security.accounts;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.function.Supplier;

public class Accounts implements UserDetailsService {
    private final AccountsRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Accounts(AccountsRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Account> byUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return byUsername(username)
                .map(AccountDetails::new)
                .orElseThrow(notFound(username));
    }

    private Supplier<UsernameNotFoundException> notFound(String username) {
        return () -> new UsernameNotFoundException("No account with username " + username);
    }

    public Account create(String username, String password) {
        String passwordHash = passwordEncoder.encode(password);
        Account account = new Account(username, passwordHash);
        return repository.save(account);
    }

    public Optional<Account> findById(Long id) {
        return repository.findById(id);
    }
}
