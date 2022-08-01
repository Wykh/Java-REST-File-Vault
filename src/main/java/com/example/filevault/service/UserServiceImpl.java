package com.example.filevault.service;

import com.example.filevault.config.UserSecurityRole;
import com.example.filevault.dto.UserDto;
import com.example.filevault.entity.ChangeRoleHistoryEntity;
import com.example.filevault.entity.RoleEntity;
import com.example.filevault.entity.UserEntity;
import com.example.filevault.exception.FileNotFoundException;
import com.example.filevault.repository.ChangeRoleHistoryRepository;
import com.example.filevault.repository.RoleRepository;
import com.example.filevault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.filevault.config.UserSecurityPermission.BLOCK;
import static com.example.filevault.config.UserSecurityPermission.CHANGE_ROLE;
import static com.example.filevault.config.UserSecurityRole.USER;
import static com.example.filevault.util.UserWorkUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ChangeRoleHistoryRepository changeRoleHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity foundUserEntity = getUserEntity(username);
        String role = foundUserEntity.getRole().getName();
        UserSecurityRole enumRole = UserSecurityRole.valueOf(role);
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
        UserEntity actorUser = getCurrentUser(userRepository);
        UserEntity targetUser = getUserEntity(username);
        UserSecurityRole userSecurityRole = UserSecurityRole.valueOf(actorUser.getRole().getName());

        List<ChangeRoleHistoryEntity> allByTarget = changeRoleHistoryRepository.findAllByTarget(actorUser);
        List<UserEntity> actorList = allByTarget.stream().map(ChangeRoleHistoryEntity::getActor).toList();

        if (newRoleAsString != null
                && userSecurityRole.getPermissions().contains(CHANGE_ROLE)
                && !actorList.contains(targetUser)) {
            UserSecurityRole newRole = UserSecurityRole.valueOf(newRoleAsString);
            RoleEntity newRoleEntity = getRoleEntity(newRole);
            ChangeRoleHistoryEntity newChangeRoleHistoryEntity =
                    new ChangeRoleHistoryEntity(actorUser, targetUser, newRoleEntity);
            changeRoleHistoryRepository.save(newChangeRoleHistoryEntity);
            targetUser.setRole(getRoleEntity(newRole));
        }
        if (isBlocked != null
                && !actorList.contains(targetUser)
                && userSecurityRole.getPermissions().contains(BLOCK)) {
            targetUser.setBlocked(isBlocked);
        }
        userRepository.save(targetUser);
        return UserDto.of(targetUser);
    }

    private UserEntity getUserEntity(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("Username %s not found", name)));
    }

    private RoleEntity getRoleEntity(UserSecurityRole roleName) {
        return roleRepository.findByName(roleName.name())
                .orElseThrow(() -> new RuntimeException("Role not found :("));
    }
}
