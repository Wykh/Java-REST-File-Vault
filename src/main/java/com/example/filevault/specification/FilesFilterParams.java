package com.example.filevault.specification;

import com.example.filevault.entity.UserEntity;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilesFilterParams {
    String name;
    Date uploadDateFrom;
    Date uploadDateTo;
    Date modifiedDateFrom;
    Date modifiedDateTo;
    List<String> extensions;
}
