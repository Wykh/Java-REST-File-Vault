package com.example.filevault.service;

import com.example.filevault.config.UserSecurityRole;
import com.example.filevault.dto.UserDto;
import com.example.filevault.entity.RoleEntity;
import com.example.filevault.entity.UserEntity;
import com.example.filevault.exception.FileNotFoundException;
import com.example.filevault.repository.RoleRepository;
import com.example.filevault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.filevault.config.UserSecurityPermission.BLOCK;
import static com.example.filevault.config.UserSecurityPermission.CHANGE_ROLE;
import static com.example.filevault.config.UserSecurityRole.USER;
import static com.example.filevault.util.UserWorkUtils.getUserSecurityRole;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity foundUserEntity = getUserEntity(username);
        String role = foundUserEntity.getRole().getName();
        UserSecurityRole enumRole = getUserSecurityRole(role);
        return new User(
                foundUserEntity.getName(),
                passwordEncoder.encode(foundUserEntity.getPassword()),
                enumRole.getGrantedAuthorities()
        );
    }

    @Override
    public UserDto registerOne(String username, String password) {
        UserEntity userEntity = UserEntity.builder()
                .name(username)
                .password(password)
                .role(getRoleEntity(USER))
                .isBlocked(false)
                .build();
        userRepository.save(userEntity);
        return UserDto.of(userEntity);
    }

    @Override
    public UserDto updateOne(String username, String newRoleAsString, Boolean isBlocked) {
        UserEntity foundUserEntity = getUserEntity(username);
        UserEntity userWhoSendRequest = getUserWhoSendRequest();
        UserSecurityRole userSecurityRole = getUserSecurityRole(userWhoSendRequest.getRole().getName());

        if (newRoleAsString != null && userSecurityRole.getPermissions().contains(CHANGE_ROLE)) {
            UserSecurityRole newRole = getUserSecurityRole(newRoleAsString);
            foundUserEntity.setRole(getRoleEntity(newRole));
        }
        if (isBlocked != null && userSecurityRole.getPermissions().contains(BLOCK)) {
            foundUserEntity.setBlocked(isBlocked);
        }
        userRepository.save(foundUserEntity);
        return UserDto.of(foundUserEntity);
    }

    private UserEntity getUserEntity(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("Username %s not found", name)));
    }

    private RoleEntity getRoleEntity(UserSecurityRole roleName) {
        return roleRepository.findByName(roleName.name())
                .orElseThrow(() -> new FileNotFoundException("Role not found :("));
    }

    private UserEntity getUserWhoSendRequest() {
        String username = ((UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getUsername();

        return userRepository.findByName(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Username %s not found", username))
        );
    }
}
