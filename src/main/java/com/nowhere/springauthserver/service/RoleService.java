package com.nowhere.springauthserver.service;

import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.persistence.entity.Role.RoleType;

import java.util.List;
import java.util.Set;

public interface RoleService {
    Role getByType(String type) throws RuntimeException;
    List<Role> getAllRoles();
}
