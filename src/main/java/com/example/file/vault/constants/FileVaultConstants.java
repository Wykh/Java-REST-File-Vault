package com.example.file.vault.constants;

public class FileVaultConstants {
    public static final String ZIP_NAME = "attachments.zip";
    public static final int MAX_FILE_SIZE_MB = 15;
    public static final long MB_TO_BYTES_MULTIPLIER = 1024 * 1024;
    public static final long MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * MB_TO_BYTES_MULTIPLIER;
    public static final double BYTES_TO_MB_MULTIPLIER = 1.0 / 1024.0 / 1024.0;

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
}
