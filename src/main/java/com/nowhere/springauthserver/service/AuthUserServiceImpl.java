package com.nowhere.springauthserver.service;

import com.nowhere.springauthserver.persistence.entity.AuthUser;
import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.persistence.repository.AuthUserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUserServiceImpl implements AuthUserService, UserDetailsService {
    private final AuthUserRepository authUserRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public AuthUserServiceImpl(RoleService roleService,
                               AuthUserRepository authUserRepository,
                               PasswordEncoder passwordEncoder) {
        this.roleService = roleService;
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public AuthUser createUser(String username, String password, List<String> roles) {
        if (username == null || password == null)
            throw new IllegalArgumentException("Username and password are required");
        Optional<AuthUser> user = authUserRepository.findByUsername((username));

        if (user.isPresent()) throw new IllegalArgumentException("User already exists");

        var encryptedPassword = passwordEncoder.encode(password);
        Set<Role> rolesE = roles.stream().map(roleService::getByType).collect(Collectors.toSet());
        var newUser = new AuthUser();
        newUser.setUsername(username);
        newUser.setRoles(rolesE);
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

    @Transactional
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
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getType().name())).collect(Collectors.toList())
        );
    }
}
