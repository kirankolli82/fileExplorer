package com.kiran.fileexplorer.core;

import com.kiran.fileexplorer.core.model.FileLocation;
import com.kiran.fileexplorer.core.model.FileSizeAttribute;
import com.kiran.fileexplorer.util.FileUtils;
import com.kiran.fileexplorer.util.LoggerUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RealtimeFileDetailsProviderTest {
    private static Logger log = LoggerUtil.contextLogger();
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    protected RealtimeFileDetailsProvider underTestRealtimeFileDetailsProvider;

    @Test
    public void testFileProviderRetrievesAllFiles() throws IOException, IllegalAccessException {
        createFileTree();
        FileLocation fileLocation = underTestRealtimeFileDetailsProvider.retrieveFileTree(Paths.get(folder.getRoot().getAbsolutePath()));
        assertEquals(folder.getRoot().getAbsolutePath(), fileLocation.getAbsolutePath());
        assertTrue(fileLocation.isDirectory());
        assertEquals(1, fileLocation.getChildren().size());
        assertTrue(fileLocation.getChildren().stream().map(FileLocation::getName).allMatch(name -> Objects.equals("DirA", name)));
        FileLocation fileLocationA = fileLocation.getChildren().get(0);
        assertTrue(fileLocationA.isDirectory());
        assertEquals(1, fileLocationA.getChildren().size());
        assertTrue(fileLocationA.getChildren().stream().map(FileLocation::getName).allMatch(name -> Objects.equals("DirB", name)));
        FileLocation fileLocationB = fileLocationA.getChildren().get(0);
        assertTrue(fileLocationB.isDirectory());
        assertEquals(3, fileLocationB.getChildren().size());
        Optional<FileLocation> dirC = fileLocationB.getChildren().stream().filter(loc -> Objects.equals("DirC", loc.getName())).findFirst();
        assertTrue(dirC.isPresent());
        Optional<FileLocation> dirD = fileLocationB.getChildren().stream().filter(loc -> Objects.equals("DirD", loc.getName())).findFirst();
        assertTrue(dirD.isPresent());
        Optional<FileLocation> dirBTxtFile = fileLocationB.getChildren().stream().filter(loc -> Objects.equals("dirBTextFile.txt", loc.getName())).findFirst();
        assertTrue(dirBTxtFile.isPresent());
        assertEquals("dirCTextFile.txt", dirC.get().getChildren().get(0).getName());
        assertEquals("dirDTextFile.txt", dirD.get().getChildren().get(0).getName());
    }

    @Test
    public void testFileSizeAttributeCheckForFileTree() throws IOException, IllegalAccessException {
        Map<String, Long> fileSizes = createFileTree();
        FileLocation fileLocation = underTestRealtimeFileDetailsProvider.retrieveFileTree(Paths.get(folder.getRoot().getAbsolutePath()));
        FileLocation fileLocationA = fileLocation.getChildren().get(0);
        assertEquals(fileSizes.get(fileLocationA.getAbsolutePath()), FileUtils.getFileSizeAttribute(fileLocationA).map(FileSizeAttribute::getSizeInBytes).orElse(-1L));
        FileLocation fileLocationB = fileLocationA.getChildren().get(0);
        assertEquals(fileSizes.get(fileLocationB.getAbsolutePath()), FileUtils.getFileSizeAttribute(fileLocationB).map(FileSizeAttribute::getSizeInBytes).orElse(-1L));
        FileLocation dirC = fileLocationB.getChildren().stream().filter(loc -> Objects.equals("DirC", loc.getName())).findFirst().get();
        assertEquals(fileSizes.get(dirC.getAbsolutePath()), FileUtils.getFileSizeAttribute(dirC).map(FileSizeAttribute::getSizeInBytes).orElse(-1L));
        FileLocation dirD = fileLocationB.getChildren().stream().filter(loc -> Objects.equals("DirD", loc.getName())).findFirst().get();
        assertEquals(fileSizes.get(dirD.getAbsolutePath()), FileUtils.getFileSizeAttribute(dirD).map(FileSizeAttribute::getSizeInBytes).orElse(-1L));
        FileLocation dirBTxtFile = fileLocationB.getChildren().stream().filter(loc -> Objects.equals("dirBTextFile.txt", loc.getName())).findFirst().get();
        assertEquals(fileSizes.get(dirBTxtFile.getAbsolutePath()), FileUtils.getFileSizeAttribute(dirBTxtFile).map(FileSizeAttribute::getSizeInBytes).orElse(-1L));
        FileLocation dirCTextFile = dirC.getChildren().get(0);
        FileLocation dirDTextFile = dirD.getChildren().get(0);
        assertEquals(fileSizes.get(dirCTextFile.getAbsolutePath()), FileUtils.getFileSizeAttribute(dirCTextFile).map(FileSizeAttribute::getSizeInBytes).orElse(-1L));
        assertEquals(fileSizes.get(dirDTextFile.getAbsolutePath()), FileUtils.getFileSizeAttribute(dirDTextFile).map(FileSizeAttribute::getSizeInBytes).orElse(-1L));
    }

    private void printSizes(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
                System.out.println(dir.toAbsolutePath().toString() + ":" + basicFileAttributes.size());
                return super.postVisitDirectory(dir, exc);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file.toAbsolutePath().toString() + ":" + attrs.size());
                System.out.println(file.getParent().getFileName().toString());
                return super.visitFile(file, attrs);
            }
        });
    }

    private Map<String, Long> createFileTree() throws IOException {
        File parent1 = folder.newFolder("DirA", "DirB", "DirC");
        File parent2 = folder.newFolder("DirA", "DirB", "DirD");
        File dirBTextFile = new File(parent1.getParentFile(), "dirBTextFile.txt");
        assertTrue(dirBTextFile.createNewFile());
        writeRandomLinesToFile(dirBTextFile);
        File dirCTextFile = new File(parent1, "dirCTextFile.txt");
        assertTrue(dirCTextFile.createNewFile());
        writeRandomLinesToFile(dirCTextFile);
        File dirDTextFile = new File(parent2, "dirDTextFile.txt");
        assertTrue(dirDTextFile.createNewFile());
        writeRandomLinesToFile(dirDTextFile);
        Map<String, Long> fileSizes = new HashMap<>();
        fileSizes.put(parent1.getAbsolutePath(), getSize(dirCTextFile));
        fileSizes.put(parent2.getAbsolutePath(), getSize(dirDTextFile));
        fileSizes.put(dirCTextFile.getAbsolutePath(), getSize(dirCTextFile));
        fileSizes.put(dirDTextFile.getAbsolutePath(), getSize(dirDTextFile));
        fileSizes.put(dirBTextFile.getAbsolutePath(), getSize(dirBTextFile));
        fileSizes.put(parent1.getParentFile().getAbsolutePath(), fileSizes.get(parent1.getAbsolutePath()) + fileSizes.get(parent2.getAbsolutePath()) + getSize(dirBTextFile));
        fileSizes.put(parent1.getParentFile().getParentFile().getAbsolutePath(), fileSizes.get(parent1.getParentFile().getAbsolutePath()));
        return fileSizes;
    }

    private long getSize(File file) throws IOException {
        return Files.readAttributes(Paths.get(file.getAbsolutePath()), BasicFileAttributes.class).size();
    }

    private void writeRandomLinesToFile(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()))) {
            Random random = new Random();
            int numLines = random.nextInt(4) + 1;
            IntStream.rangeClosed(0, numLines).forEach(value -> {
                try {
                    writer.write("Line number" + value);
                } catch (IOException e) {
                    Assert.fail(e.getMessage());
                }
            });
        }
    }


}
