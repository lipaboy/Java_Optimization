package ru.nsu.fit.g14201.lipatkin.lab8;

/**
 * Created by castiel on 12.12.2017.
 */
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Starter {

    private static final int LITTLE_SIZE = 2;
    private static final int TOTAL_SIZE = (int) (1024 * 1024 * 1024 * 2L - LITTLE_SIZE);

    static final int STRING_SIZE = 77;
    static final int TOTAL_STRINGS = TOTAL_SIZE / STRING_SIZE;

    private final FileChannel outputChannel;

    private final Sorter sorter;

    private final ByteBuffer littleBuffer = ByteBuffer.allocate(LITTLE_SIZE + 1);
    final ByteBuffer sortBuffer;

    public Starter(String inputPath, String outputPath) throws IOException {
        final RandomAccessFile inputFile = new RandomAccessFile(inputPath, "rw");
        sortBuffer = inputFile.getChannel().map(FileChannel.MapMode.PRIVATE,
                0, TOTAL_SIZE);

        inputFile.seek(TOTAL_SIZE);
        inputFile.read(littleBuffer.array());
        littleBuffer.array()[LITTLE_SIZE] = '\n';

        Files.deleteIfExists(Paths.get(outputPath));
        outputChannel = new RandomAccessFile(outputPath, "rw").getChannel();

        sorter = new QuickSorter(this, 8);
    }

    public void start() throws IOException {
        sorter.sort();

        sortBuffer.limit(findLittlePos() * STRING_SIZE);
        while (sortBuffer.hasRemaining()) {
            outputChannel.write(sortBuffer);
        }

        outputChannel.write(littleBuffer);

        sortBuffer.limit(TOTAL_SIZE);
        while (sortBuffer.hasRemaining()) {
            outputChannel.write(sortBuffer);
        }
    }

    private int findLittlePos() {
        int low = 0;
        int high = TOTAL_STRINGS;

        while (high - low > 1) {
            int mid = (low + high) / 2;

            if (cmpLittle(mid) < 0)
                low = mid;
            else
                high = mid;
        }

        if (cmpLittle(low) < 0)
            ++low;
        return low;
    }

    private int cmpLittle(int pos) {
        pos *= STRING_SIZE;

        int res = sortBuffer.get(pos) - littleBuffer.get(0);
        if (res != 0)
            return res;

        return sortBuffer.get(pos + 1) - littleBuffer.get(1);
    }
}