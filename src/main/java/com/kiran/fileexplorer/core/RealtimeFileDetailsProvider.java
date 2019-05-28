package com.kiran.fileexplorer.core;

import com.kiran.fileexplorer.core.model.FileLocation;

import java.io.IOException;
import java.nio.file.Path;

public interface RealtimeFileDetailsProvider {

    FileLocation retrieveFileTree(Path path) throws IllegalAccessException, IOException;
}
