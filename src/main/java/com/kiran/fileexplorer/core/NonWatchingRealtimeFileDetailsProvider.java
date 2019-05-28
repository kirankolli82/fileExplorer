package com.kiran.fileexplorer.core;

import com.kiran.fileexplorer.core.model.FileLocation;
import com.kiran.fileexplorer.core.model.FileSizeAttribute;
import com.kiran.fileexplorer.util.FileUtils;
import com.kiran.fileexplorer.util.LoggerUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

public class NonWatchingRealtimeFileDetailsProvider implements RealtimeFileDetailsProvider {

    private static Logger log = LoggerUtil.contextLogger();
    //The conscious decision to not use FileVisitor is based on the fact that the FileVisitor interface
    //does not expose strategies for File Tree traversal like DFS or BFS. In the absence of this it would
    //introduce complexity to work around unknown traversal strategies to create a file tree graph

    @Override
    public FileLocation retrieveFileTree(Path path) throws IllegalAccessException, IOException {
        if (!Files.exists(path)) {
            throw new IllegalAccessException("Path provided to traverse does not exist : " + path.toAbsolutePath().toString());
        }
        return traversePathDFS(getFileLocation(path, null));
    }

    private FileLocation traversePathDFS(FileLocation fileLocation) throws IOException {
        File file = new File(fileLocation.getAbsolutePath());
        if (file.isDirectory()) {
            for (String name : Optional.ofNullable(file.list()).orElse(new String[0])) {
                FileLocation child = getFileLocation(Paths.get(fileLocation.getAbsolutePath() + File.separator + name), fileLocation);
                fileLocation.addChild(child);
                traversePathDFS(child);
            }
            long totalSize = fileLocation.getChildren().stream()
                    .map(fileLocation1 -> FileUtils.getFileSizeAttribute(fileLocation1))
                    .map(fileSizeAttribute -> fileSizeAttribute.map(fileSizeAttribute1 -> fileSizeAttribute1.getSizeInBytes()))
                    .map(aLong -> aLong.orElse(0L))
                    .reduce(Long::sum).orElse(0L);
            fileLocation.addFileAttribute(new FileSizeAttribute(totalSize));
            return fileLocation;
        } else {
            try {
                BasicFileAttributes basicFileAttributes = Files.readAttributes(Paths.get(fileLocation.getAbsolutePath()), BasicFileAttributes.class);
                fileLocation.addFileAttribute(new FileSizeAttribute(basicFileAttributes.size()));
                return fileLocation;
            } catch (Exception e) {
                log.error("Error while accessing file {}. File attributes will not be included in results", fileLocation.getAbsolutePath());
                return fileLocation;
            }
        }
    }

    private static FileLocation getFileLocation(Path path, FileLocation parent) {
        String parentPath = Optional.ofNullable(path.getParent()).map(path1 -> path1.toAbsolutePath().toString()).orElse(null);
        String fileName = Optional.ofNullable(path.getFileName()).orElse(path.getRoot()).toString();
        return new FileLocation(fileName, Files.isDirectory(path), parentPath, parent);
    }
}
