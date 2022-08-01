package com.example.filevault.service;

import com.example.filevault.dto.FileBytesAndNameById;
import com.example.filevault.specification.FilesFilterParams;
import com.example.filevault.dto.FileDto;
import com.example.filevault.dto.FileNameById;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileService {
    FileDto upload(MultipartFile file, String comment);
    List<FileDto> getAll(FilesFilterParams filterParams);
    List<FileNameById> getNames();
    FileDto getDTOById(UUID id);
    FileBytesAndNameById getBytesAndNameById(UUID id);
    FileBytesAndNameById getZipBytesByIds(List<UUID> id);
    FileDto update(UUID id, String newFileName, String newComment, Boolean isPublic);
    FileDto delete(UUID id);

}
