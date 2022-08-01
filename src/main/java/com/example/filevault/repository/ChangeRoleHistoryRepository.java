package com.example.filevault.repository;

import com.example.filevault.entity.ChangeRoleHistoryEntity;
import com.example.filevault.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChangeRoleHistoryRepository extends JpaRepository<ChangeRoleHistoryEntity, UUID> {
    List<ChangeRoleHistoryEntity> findAllByActor(UserEntity user);
    List<ChangeRoleHistoryEntity> findAllByTarget(UserEntity user);
}
