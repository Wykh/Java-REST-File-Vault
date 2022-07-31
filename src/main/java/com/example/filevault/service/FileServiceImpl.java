package com.example.filevault.service;

import com.example.filevault.config.UserSecurityPermission;
import com.example.filevault.config.UserSecurityRole;
import com.example.filevault.constants.FileVaultConstants;
import com.example.filevault.dto.FileBytesAndNameById;
import com.example.filevault.dto.FileDto;
import com.example.filevault.dto.FileNameById;
import com.example.filevault.entity.FileEntity;
import com.example.filevault.entity.UserEntity;
import com.example.filevault.exception.FileNotFoundException;
import com.example.filevault.exception.TooLargeFileSizeException;
import com.example.filevault.repository.FileRepository;
import com.example.filevault.repository.UserRepository;
import com.example.filevault.specification.FileSpecification;
import com.example.filevault.specification.FilesFilterParams;
import com.example.filevault.util.FileNameUtils;
import com.example.filevault.util.FileSizeUtils;
import com.example.filevault.util.FileWorkUtils;
import com.example.filevault.util.UserWorkUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.example.filevault.config.UserSecurityPermission.CHANGE_FILE_ACCESS;
import static com.example.filevault.config.UserSecurityPermission.DELETE_PUBLIC_FILE;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final Path rootLocation = Paths.get(FileVaultConstants.STORAGE_LOCATION);

    @Override
    public FileDto upload(MultipartFile file, String passedComment) {
        if (FileSizeUtils.toMB(file.getSize()) >= FileVaultConstants.MAX_FILE_SIZE_MB)
            throw new TooLargeFileSizeException("File Size Cant be more than " + FileVaultConstants.MAX_FILE_SIZE_MB + "MB");

        String fullFileName = file.getOriginalFilename();
        String fileName = FileNameUtils.getNameWithoutExtension(fullFileName);
        String fileExtension = FileNameUtils.getExtension(fullFileName);

        FileEntity fullFilledNewEntity = fileRepository.save(
                FileEntity.builder()
                        .name(fileName)
                        .extension(fileExtension)
                        .comment(passedComment)
                        .contentFolderPath(FileVaultConstants.STORAGE_LOCATION)
                        .size(file.getSize())
                        .user(getUserWhoSendRequest())
                        .isPublic(false)
                        .build()
        );
        Path destinationFilePath = rootLocation.resolve(
                fullFilledNewEntity.getId().toString() + '.' + fileExtension);

        FileWorkUtils.saveFileToSystem(file, destinationFilePath);

        return FileDto.of(fullFilledNewEntity);
    }

    @Override
    public List<FileDto> getAll(FilesFilterParams filterParams) {
        return getFileEntityStream(filterParams)
                .map(FileDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileNameById> getNames() {
        FilesFilterParams emptyParams = FilesFilterParams.builder().build();
        return getFileEntityStream(emptyParams)
                .map(FileNameById::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FileDto getDTOById(UUID id) {
        FileEntity foundEntity = getFileEntity(id, true);
        return FileDto.of(foundEntity);
    }

    @Override
    public FileBytesAndNameById getBytesAndNameById(UUID id) {
        FileEntity foundEntity = getFileEntity(id, true);

        Path fileLocation = rootLocation.resolve(foundEntity.getId().toString() + '.' + foundEntity.getExtension());
        byte[] fileContent = FileWorkUtils.getFileContent(fileLocation);

        return FileBytesAndNameById.of(foundEntity, fileContent);
    }

    @Override
    public FileBytesAndNameById getZipBytesByIds(List<UUID> ids) {
        // TODO: stream output with byte chunks
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ZipOutputStream zipOut = new ZipOutputStream(bos)) {
                zipOut.setLevel(ZipOutputStream.STORED);

                Set<String> names = new HashSet<>();
                for (UUID id : ids) {
                    FileBytesAndNameById fileToDownload = getBytesAndNameById(id);

                    String fullFileName = FileNameUtils.getUniqueFileName(names, fileToDownload.getName(), fileToDownload.getExtension());
                    zipOut.putNextEntry(new ZipEntry(fullFileName));
                    zipOut.write(fileToDownload.getContent());
                }
            }
            return FileBytesAndNameById.of(FileVaultConstants.ZIP_ENTITY, bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: custom exception
        }
    }

    @Override
    public FileDto update(UUID id, String newFileName, String newComment, Boolean isPublic) {
        FileEntity foundEntity = getFileEntity(id, false, CHANGE_FILE_ACCESS);

        foundEntity.setName(newFileName);
        foundEntity.setComment(newComment);
        foundEntity.setPublic(isPublic);
        return FileDto.of(fileRepository.save(foundEntity));
    }

    @Override
    public FileDto delete(UUID id) {
        FileEntity foundFileEntity = getFileEntity(id, false, DELETE_PUBLIC_FILE);

        Path fileLocation = rootLocation.resolve(
                foundFileEntity.getId().toString() + '.' + foundFileEntity.getExtension());
        fileRepository.deleteById(id);
        try {
            Files.delete(fileLocation);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: custom exception
        }
        FileDto deletedDto = FileDto.of(foundFileEntity);
        deletedDto.setDownloadUrl("");
        return deletedDto;
    }

    private Stream<FileEntity> getFileEntityStream(FilesFilterParams filterParams) {
        return fileRepository.findAll(FileSpecification.getFilteredFiles(filterParams,
                getUserWhoSendRequest())).stream();
    }

    private FileEntity getFileEntity(UUID id, boolean canBePublic, UserSecurityPermission... permissions) {
        FileEntity foundEntity = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found :("));

        UserEntity userWhoSendRequest = getUserWhoSendRequest();
        UserSecurityRole userSecurityRole = UserWorkUtils.getUserSecurityRole(userWhoSendRequest.getRole().getName());
        if (!(canBePublic && foundEntity.isPublic()) &&
                Arrays.stream(permissions).noneMatch(permission
                        -> userSecurityRole.getPermissions().contains(permission)) &&
                !foundEntity.getUser().equals(userWhoSendRequest))
            throw new UsernameNotFoundException("Can't get access to this file");
        return foundEntity;
    }

    private UserEntity getUserWhoSendRequest() {
        String username = ((UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getUsername();

        return userRepository.findByName(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Username %s not found", username))
        );
    }
}