package com.kiran.fileexplorer.core.model;

public class FileSizeAttribute implements FileAttribute {
    private final long sizeInBytes;

    public FileSizeAttribute(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    @Override
    public String toString() {
        return "FileSizeAttribute{" +
                "sizeInBytes=" + sizeInBytes +
                '}';
    }
}
