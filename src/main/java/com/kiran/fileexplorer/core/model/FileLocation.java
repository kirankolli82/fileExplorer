package com.kiran.fileexplorer.core.model;


import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileLocation {

    private final String name;
    private final boolean isDir;
    private final String parentPath;
    private final FileLocation parent;
    private final List<FileLocation> children = new ArrayList<>();
    private final List<FileAttribute> fileAttributes = new ArrayList<>();

    public FileLocation(String name, boolean isDir, String parentPath, FileLocation parent) {
        this.name = name;
        this.isDir = isDir;
        this.parentPath = parentPath;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public String getParentPath() {
        return parentPath;
    }

    public String getAbsolutePath() {
        if (StringUtils.isBlank(this.parentPath)) {
            return this.name;
        } else {
            return this.getParentPath() + File.separator + name;
        }
    }

    public boolean isDirectory() {
        return this.isDir;
    }

    public List<FileLocation> getChildren() {
        return new ArrayList<>(children);
    }

    public void addChild(FileLocation fileLocation) {
        this.children.add(fileLocation);
    }

    public void addFileAttribute(FileAttribute fileAttribute) {
        this.fileAttributes.add(fileAttribute);
    }

    public List<FileAttribute> getFileAttributes() {
        return new ArrayList<>(fileAttributes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileLocation that = (FileLocation) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(parentPath, that.parentPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parentPath);
    }

    @Override
    public String toString() {
        return "FileLocation{" +
                "name='" + name + '\'' +
                ", parentPath='" + parentPath + '\'' + System.lineSeparator() +
                ", children=" + children.stream().map(FileLocation::getName).collect(Collectors.joining(" , ")) + System.lineSeparator() +
                ", fileAttributes=" + fileAttributes +
                '}';
    }
}
