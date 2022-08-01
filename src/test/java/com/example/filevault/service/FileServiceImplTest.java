package com.example.filevault.service;

import com.example.filevault.constants.FileVaultConstants;
import com.example.filevault.dto.FileDto;
import com.example.filevault.entity.FileEntity;
import com.example.filevault.exception.BadFileTypeException;
import com.example.filevault.exception.EmptyFileNameException;
import com.example.filevault.exception.TooLargeFileSizeException;
import com.example.filevault.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    FileRepository fileRepository;

    @InjectMocks
    FileServiceImpl fileService;

    @Test
    void upload_Success_shouldUploadFile_whenAllIsOk()  {
        // arrange
        String testName = "helloworld";
        String testExtension = "txt";
        String testComment = "tcomment";
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                testName, testName + '.' + testExtension, MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes());
        FileEntity beforeSave = FileEntity.builder()
                .name(testName)
                .extension(testExtension)
                .comment(testComment)
                .contentFolderPath(FileVaultConstants.STORAGE_LOCATION)
                .size(testMultipartFile.getSize())
                .build();
        FileEntity afterSave = FileEntity.builder()
                .id(UUID.fromString("1-1-1-1-1"))
                .name(testName)
                .extension(testExtension)
                .comment(testComment)
                .modifiedDate(new Date())
                .uploadDate(new Date())
                .contentFolderPath(FileVaultConstants.STORAGE_LOCATION)
                .size(testMultipartFile.getSize())
                .build();
        FileDto expectedFileDto = FileDto.of(afterSave);
        when(fileRepository.save(beforeSave)).thenReturn(afterSave);

        //act
        FileDto actualFileDto = fileService.upload(testMultipartFile, testComment);

        // assert
        verify(fileRepository).save(beforeSave);
        assertThat(actualFileDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedFileDto);
        assertThat(
                Paths.get(FileVaultConstants.STORAGE_LOCATION)
                        .resolve(afterSave.getId().toString() + '.' + afterSave.getExtension())
                        .toFile().exists())
                .isTrue();
    }

    @Test
    void upload_Fail_shouldThrowException_whenFileSizeMoreThanMaxSize() {
        // arrange
        String testName = "tname";
        long tooMuchMB = FileVaultConstants.MAX_FILE_SIZE_BYTES + 1;
        byte[] testContent = new byte[(int) tooMuchMB];

        MockMultipartFile testMultipartFile = new MockMultipartFile(testName, testContent);

        // act, assert
        assertThatExceptionOfType(TooLargeFileSizeException.class)
                .isThrownBy(() -> fileService.upload(testMultipartFile, "Test comment"));
    }

    @Test
    void upload_Fail_shouldThrowException_whenBadFileName() {
        // TODO: Test png, img and other extensions
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "foo", "foo.", MediaType.ALL_VALUE, new byte[1]);

        assertThatExceptionOfType(BadFileTypeException.class)
                .isThrownBy(() -> fileService.upload(testMultipartFile, "Test comment"));
    }

    @Test
    void upload_Fail_shouldThrowException_whenFileNameIsEmpty() {
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "foo", "", MediaType.ALL_VALUE, new byte[1]);

        assertThatExceptionOfType(EmptyFileNameException.class)
                .isThrownBy(() -> fileService.upload(testMultipartFile, "Test comment"));
    }

    @Test
    void update_shouldUpdateFile_whenFileFound() {
        // arrange
        UUID idSample = UUID.fromString("1-1-1-1-1");
        String testExtension = "txt";
        String nameBefore = "helloworld";
        String commentBefore = "tcomment";
        String nameAfter = "newName";
        String commentAfter = "newComment";
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                nameBefore, nameBefore + '.' + testExtension, MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes());
        FileEntity beforeUpdate = FileEntity.builder()
                .id(idSample)
                .name(nameBefore)
                .extension(testExtension)
                .comment(commentBefore)
                .modifiedDate(new Date())
                .uploadDate(new Date())
                .contentFolderPath(FileVaultConstants.STORAGE_LOCATION)
                .size(testMultipartFile.getSize())
                .build();
        FileEntity afterUpdate = FileEntity.builder()
                .id(idSample)
                .name(nameAfter)
                .extension(testExtension)
                .comment(commentAfter)
                .modifiedDate(new Date())
                .uploadDate(new Date())
                .contentFolderPath(FileVaultConstants.STORAGE_LOCATION)
                .size(testMultipartFile.getSize())
                .build();
        FileDto expectedFileDto = FileDto.of(afterUpdate);
        when(fileRepository.findById(idSample)).thenReturn(Optional.of(beforeUpdate));
        when(fileRepository.save(any())).thenReturn(afterUpdate);

        // act
        FileDto actualFileDto = fileService.update(idSample, nameAfter, commentAfter, false);

        // assert
        verify(fileRepository).save(afterUpdate);
        assertThat(actualFileDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedFileDto);
    }

    @Test
    void delete_shouldDeleteFile_whenFileFound() {
        // arrange
        UUID idSample = UUID.fromString("1-1-1-1-1");
        String testName = "helloworld";
        String testExtension = "txt";
        String testComment = "tcomment";
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                testName, testName + '.' + testExtension, MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes());
        FileEntity beforeSave = FileEntity.builder()
                .name(testName)
                .extension(testExtension)
                .comment(testComment)
                .contentFolderPath(FileVaultConstants.STORAGE_LOCATION)
                .size(testMultipartFile.getSize())
                .build();
        FileEntity afterSave = FileEntity.builder()
                .id(idSample)
                .name(testName)
                .extension(testExtension)
                .comment(testComment)
                .modifiedDate(new Date())
                .uploadDate(new Date())
                .contentFolderPath(FileVaultConstants.STORAGE_LOCATION)
                .size(testMultipartFile.getSize())
                .build();
        FileDto expectedFileDto = FileDto.of(afterSave);
        expectedFileDto.setDownloadUrl("");
        when(fileRepository.save(any())).thenReturn(afterSave);
        when(fileRepository.findById(idSample)).thenReturn(Optional.of(afterSave));

        //act
        fileService.upload(testMultipartFile, testComment);
        FileDto deletedFileDto = fileService.delete(idSample);

        // assert
        verify(fileRepository).save(beforeSave);
        verify(fileRepository).deleteById(idSample);
        assertThat(deletedFileDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedFileDto);
        assertThat(
                Paths.get(FileVaultConstants.STORAGE_LOCATION)
                        .resolve(afterSave.getId().toString() + '.' + afterSave.getExtension())
                        .toFile().exists())
                .isFalse();
    }
}
