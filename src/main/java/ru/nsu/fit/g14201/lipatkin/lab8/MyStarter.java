package ru.nsu.fit.g14201.lipatkin.lab8;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by castiel on 13.12.2017.
 */
public class MyStarter {

    static final int STRING_SIZE = 77;
    private static final long __TOTAL_SIZE = //1024 * 1024 * 512L;
                                                100 *  77;
    private static final int LITTLE_SIZE = (int) __TOTAL_SIZE % 77;
    private static final int TOTAL_SIZE = (int) (__TOTAL_SIZE - LITTLE_SIZE);

    static final int TOTAL_STRINGS = TOTAL_SIZE / STRING_SIZE;
    private static final int PARTITION_COUNT = 2;
    private int[] partitionSizes;
    private FileChannel[] tempChannels;
    private String[] tempFilenames;
    {
        partitionSizes = new int[PARTITION_COUNT];
        int mainPart = TOTAL_SIZE / PARTITION_COUNT;
        int remainder = TOTAL_SIZE % PARTITION_COUNT;
        for (int i = 0; i < PARTITION_COUNT; i++) {
            partitionSizes[i] = mainPart;
        }
        partitionSizes[0] += remainder;

        tempChannels = new FileChannel[PARTITION_COUNT];
        tempFilenames = new String[PARTITION_COUNT];
        for (int i = 0; i < tempFilenames.length; i++)
            tempFilenames[i] = new String("temp" + i + ".dat");
    }

    public MyStarter() {}

    public void start(String inputPath, String outputPath) throws IOException {
        FileChannel outputChannel;
        final RandomAccessFile inputFile = new RandomAccessFile(inputPath, "rw");

        int currentPosition = 0;

        for (int i = 0; i < partitionSizes.length; i++) {

            MappedByteBuffer sortBuffer = inputFile.getChannel().map(FileChannel.MapMode.PRIVATE,
                    currentPosition, partitionSizes[i]);

            //inputFile.seek(currentPosition + partitionSizes[i]);

            Files.deleteIfExists(Paths.get(tempFilenames[i]));
            tempChannels[i] = new RandomAccessFile(tempFilenames[i], "rw").getChannel();

            new CombSorter(sortBuffer, STRING_SIZE, partitionSizes[i], 2).sort();
            tempChannels[i].write(sortBuffer);

            currentPosition += partitionSizes[i];

        }

//        ByteBuffer[] tempBuffers = new ByteBuffer[PARTITION_COUNT];
//        for (int i = 0; i < tempBuffers.length; i++) {
//            tempBuffers[i] = ByteBuffer.allocate(100);
//            tempChannels[i].read(tempBuffers[i]);
//            tempBuffers[i].flip();
//        }
//
//        for (;;) {
//            int maxInd = 0;
//            for (int j = 0; j < tempBuffers.length; j++) {
//                if (tempBuffers[i].compareTo)
//            }
//        }

        //Files.deleteIfExists(Paths.get(outputPath));
        //outputChannel = new RandomAccessFile(outputPath, "rw").getChannel();

        //for (int i = 0; i < PARTITION_COUNT; i++)
           // Files.delete(Paths.get(tempFilenames[i]));
    }

}
