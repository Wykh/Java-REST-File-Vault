package com.example.filevault.specification;

import com.example.filevault.config.UserSecurityRole;
import com.example.filevault.entity.FileEntity;
import com.example.filevault.entity.UserEntity;
import com.example.filevault.util.UserWorkUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

import static com.example.filevault.config.UserSecurityPermission.FILE_READ_ALL;

public class FileSpecification {

    public static Specification<FileEntity> getFilteredFiles(FilesFilterParams params,
                                                             UserEntity userToFilter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params.getName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + params.getName().toLowerCase() + "%"));
            }
            if (params.getModifiedDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("modifiedDate"), params.getModifiedDateFrom()));
            }
            if (params.getModifiedDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("modifiedDate"), params.getModifiedDateTo()));
            }
            if (params.getUploadDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("uploadDate"), params.getUploadDateFrom()));
            }
            if (params.getUploadDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("uploadDate"), params.getUploadDateTo()));
            }
            if (params.getExtensions() != null) {
                Expression<String> parentExpression = root.get("extension");
                predicates.add(parentExpression.in(params.getExtensions()));
            }

            if (userToFilter != null) {
                UserSecurityRole userSecurityRole = UserSecurityRole.valueOf(userToFilter.getRole().getName());
                boolean haveUserPermission = userSecurityRole.getPermissions().contains(FILE_READ_ALL);

                if (!haveUserPermission) {
                    Predicate belongsToUser = criteriaBuilder.equal(root.get("user"), userToFilter);
                    Predicate isPublic = criteriaBuilder.equal(root.get("isPublic"), true);
                    predicates.add(criteriaBuilder.or(belongsToUser, isPublic));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
