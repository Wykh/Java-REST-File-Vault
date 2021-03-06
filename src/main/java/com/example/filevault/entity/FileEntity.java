package com.example.filevault.entity;

import lombok.*;

import javax.persistence.*;
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
    @Id
    private UUID id;
    private String name;
    private String extension;
    private String comment;
    private Date uploadDate;
    private Date modifiedDate;
    private String contentFolderPath;
    private long size;
}
