package com.example.filevault.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ChangeRoleHistoryEntity {
    @Id
    @Column(nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="actor_user_id", referencedColumnName = "id", nullable = false)
    @NonNull
    private UserEntity actor;

    @ManyToOne
    @JoinColumn(name="target_user_id", referencedColumnName = "id", nullable = false)
    @NonNull
    private UserEntity target;

    @ManyToOne
    @JoinColumn(name="role_id", referencedColumnName = "id", nullable = false)
    @NonNull
    private RoleEntity role;
}
