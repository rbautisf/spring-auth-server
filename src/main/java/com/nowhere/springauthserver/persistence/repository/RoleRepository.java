package com.nowhere.springauthserver.persistence.repository;

import com.nowhere.springauthserver.persistence.entity.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query("Select r from Role r where r.type = :type")
    Optional<Role> findByType(Role.RoleType type);

}
