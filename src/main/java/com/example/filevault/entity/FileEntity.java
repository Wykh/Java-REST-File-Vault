package com.example.filevault.entity;

import com.example.filevault.constants.FileVaultConstants;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Generated;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FileEntity {
    // TODO: Set toString, equals, and hashCode - https://stackoverflow.com/questions/34241718/lombok-builder-and-jpa-default-constructor
    // TODO: Add hibernate annotations for default values
    @Id
    @Column(nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, updatable=false)
    private String extension;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false, updatable=false)
    @CreationTimestamp
    private Date uploadDate;

    @Column(nullable = false)
    @UpdateTimestamp
    private Date modifiedDate;

    @Column(nullable = false, updatable=false)
    private String contentFolderPath;

    @Column(nullable = false, updatable=false)
    private long size;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private boolean isPublic;

}
