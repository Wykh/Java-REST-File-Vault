package com.example.filevault.config;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.filevault.config.UserSecurityPermission.*;

public enum UserSecurityRole {
    USER(Sets.newHashSet(FILE_READ, FILE_WRITE)),
    MANAGER(Sets.newHashSet(FILE_READ, FILE_WRITE, CHANGE_FILE_ACCESS, BLOCK)),
    ADMIN(Sets.newHashSet(FILE_READ, FILE_WRITE, FILE_READ_ALL, CHANGE_FILE_ACCESS, BLOCK, CHANGE_ROLE, DELETE_PUBLIC_FILE)),
    SUPERADMIN(Sets.newHashSet(FILE_READ, FILE_WRITE, USER_READ, USER_WRITE));

    private final Set<UserSecurityPermission> permissions;

    UserSecurityRole(Set<UserSecurityPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserSecurityPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
