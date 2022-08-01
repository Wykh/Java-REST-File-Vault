package com.example.filevault.dto;

import com.example.filevault.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private String name;
    private String role;
    private boolean isBlocked;

    public static UserDto of (UserEntity entity) {
        return new UserDto(entity.getName(), entity.getRole().getName(), entity.isBlocked());
    }
}
