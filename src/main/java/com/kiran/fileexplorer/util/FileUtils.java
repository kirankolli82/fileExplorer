package com.kiran.fileexplorer.util;

import com.kiran.fileexplorer.core.model.FileLocation;
import com.kiran.fileexplorer.core.model.FileSizeAttribute;

import java.util.Optional;

public class FileUtils {

    public static Optional<FileSizeAttribute> getFileSizeAttribute(FileLocation fileLocation) {
        return fileLocation.getFileAttributes().stream().filter(fileAttribute -> fileAttribute instanceof FileSizeAttribute).map(fileAttribute -> ((FileSizeAttribute) fileAttribute)).findFirst();
    }
}
