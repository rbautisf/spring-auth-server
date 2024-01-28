package com.nowhere.springauthserver.persistence.repository;

import com.nowhere.springauthserver.persistence.entity.AuthUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
    @Query("Select u from AuthUser u where u.username = :username")
    Optional<AuthUser> findByUsername(String username);
}
