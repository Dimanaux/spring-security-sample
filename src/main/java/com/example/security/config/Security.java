package com.example.security.config;

import com.example.security.accounts.Accounts;
import com.example.security.accounts.AccountsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Security extends WebSecurityConfigurerAdapter {
    private final AccountsRepository accountsRepository;

    public Security(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/join").permitAll()
                .antMatchers("/welcome").authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .failureUrl("/login?error=Wrong credentials")
                .permitAll()
                .defaultSuccessUrl("/welcome")
                .and()
            .logout()
                .logoutSuccessUrl("/login")
                .permitAll();
        // @formatter:on
    }

    @Bean
    public Accounts accounts() {
        return new Accounts(accountsRepository, passwordEncoder());
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(accounts());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }
}
