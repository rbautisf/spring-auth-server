package com.nowhere.springauthserver.service;

import com.nowhere.springauthserver.persistence.entity.AuthUser;
import com.nowhere.springauthserver.persistence.repository.AuthUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthUserServiceImpl implements AuthUserService, UserDetailsService {
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthUserServiceImpl(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public AuthUser createUser(String username, String password) {
        if (username == null || password == null)
            throw new IllegalArgumentException("Username and password are required");
        Optional<AuthUser> user = authUserRepository.findByUsername((username));

        if (user.isPresent()) throw new IllegalArgumentException("User already exists");

        var encryptedPassword = passwordEncoder.encode(password);
        var newUser = new AuthUser();
        newUser.setUsername(username);
        newUser.setPassword(encryptedPassword);
        newUser.setEnabled(true);
        newUser.setAccountNonExpired(true);
        newUser.setAccountNonLocked(true);
        newUser.setCredentialsNonExpired(true);
        return authUserRepository.save(newUser);
    }

    @Override
    public AuthUser getByUsername(String username) {
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser user = getByUsername(username);
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                java.util.Collections.emptyList()
        );
    }
}
