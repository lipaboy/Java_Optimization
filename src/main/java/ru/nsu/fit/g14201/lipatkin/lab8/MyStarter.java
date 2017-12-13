package ru.nsu.fit.g14201.lipatkin.lab8;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by castiel on 13.12.2017.
 */
public class MyStarter {

    static final int STRING_SIZE = 77;
    private static final long __TOTAL_SIZE = 1024 * 1024 * 128L;
    private static final int LITTLE_SIZE = (int) __TOTAL_SIZE % 77;
    private static final int TOTAL_SIZE = (int) (__TOTAL_SIZE - LITTLE_SIZE);

    static final int TOTAL_STRINGS = TOTAL_SIZE / STRING_SIZE;

    private final FileChannel outputChannel;

    private final CombSorter sorter;

    final ByteBuffer sortBuffer;

    public MyStarter(String inputPath, String outputPath) throws IOException {
        final RandomAccessFile inputFile = new RandomAccessFile(inputPath, "rw");
        sortBuffer = inputFile.getChannel().map(FileChannel.MapMode.PRIVATE,
                0, TOTAL_SIZE);

        inputFile.seek(TOTAL_SIZE);

        Files.deleteIfExists(Paths.get(outputPath));
        outputChannel = new RandomAccessFile(outputPath, "rw").getChannel();

        sorter = new CombSorter(this, 1);
    }

    public void start() throws IOException {
        sorter.sort();

        outputChannel.write(sortBuffer);
    }

}
