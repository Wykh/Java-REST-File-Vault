package com.example.filevault.config;

public enum UserSecurityPermission {
    FILE_READ("file:read"),
    FILE_WRITE("file:write"),
    FILE_READ_ALL("file:read_all"),
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    CHANGE_ROLE("extra:change_role"),
    CHANGE_FILE_ACCESS("extra:change_file_access"),
    DELETE_PUBLIC_FILE("extra:delete_public_file"),
    BLOCK("extra:block"),
    NULL("null:null");

    private final String permission;

    UserSecurityPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}