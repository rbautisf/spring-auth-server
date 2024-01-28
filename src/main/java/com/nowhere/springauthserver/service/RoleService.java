package com.nowhere.springauthserver.service;

import com.nowhere.springauthserver.persistence.entity.Role;
import java.util.List;

public interface RoleService {
    Role getByType(String type) throws RuntimeException;

    List<Role> getAllRoles();
}
