package com.example.filevault.util;

import com.example.filevault.config.UserSecurityRole;
import com.example.filevault.entity.UserEntity;
import com.example.filevault.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.example.filevault.config.UserSecurityRole.*;
import static com.example.filevault.config.UserSecurityRole.USER;

@Slf4j
public class UserWorkUtils {
    public static UserSecurityRole getUserSecurityRole(String role) {
        UserSecurityRole enumRole;
        switch (role) {
            case "SUPERADMIN" -> enumRole = SUPERADMIN;
            case "ADMIN" -> enumRole = ADMIN;
            case "USER" -> enumRole = USER;
            case "MANAGER" -> enumRole = MANAGER;
            default -> {
                log.info("Role " + role + " not found, using default 'USER' role");
                enumRole = USER;
            }
        }
        return enumRole;
    }

    public static UserEntity getCurrentUser(UserRepository userRepository) {
        String username = ((UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getUsername();

        return userRepository.findByName(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Username %s not found", username))
        );
    }
}
