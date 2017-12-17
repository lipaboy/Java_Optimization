package ru.nsu.fit.g14201.lipatkin.lab8;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Created by castiel on 13.12.2017.
 */
public class MyStarter {
    static private final Logger log = Logger.getLogger(MyStarter.class.getName());

    static final int STRING_SIZE = 77;
    private static final long __TOTAL_SIZE = //1024 * 1024 * 512L;
                                                //100 *  STRING_SIZE;
                                            1024;
    private static final int LITTLE_SIZE = (int) __TOTAL_SIZE % STRING_SIZE;
    private static final int TOTAL_SIZE = (int) (__TOTAL_SIZE - LITTLE_SIZE);

    static final int TOTAL_STRINGS = TOTAL_SIZE / STRING_SIZE;
    private static final int PARTITION_COUNT = 2;
    private int[] partitionSizes;
    private ArrayList<FileChannel> tempChannels;
    private String[] tempFilenames;
    {
        partitionSizes = new int[PARTITION_COUNT];
        int mainPart = TOTAL_SIZE / PARTITION_COUNT;
        int remainder = TOTAL_SIZE % PARTITION_COUNT;
        for (int i = 0; i < PARTITION_COUNT; i++) {
            partitionSizes[i] = mainPart;
        }
        partitionSizes[0] += remainder;

        tempChannels = new ArrayList<>(PARTITION_COUNT);
        tempFilenames = new String[PARTITION_COUNT];
        for (int i = 0; i < tempFilenames.length; i++) {
            tempChannels.add(null);
            tempFilenames[i] = new String("temp" + i + ".dat");
        }
    }

    public MyStarter() {}

    public void start(String inputPath, String outputPath) throws IOException {
        LocalTime startTime = LocalTime.now();
        FileChannel outputChannel;
        final RandomAccessFile inputFile = new RandomAccessFile(inputPath, "rw");

        int currentPosition = 0;

        for (int i = 0; i < partitionSizes.length; i++) {

            MappedByteBuffer sortBuffer = inputFile.getChannel().map(FileChannel.MapMode.PRIVATE,
                    currentPosition, partitionSizes[i]);

            //inputFile.seek(currentPosition + partitionSizes[i]);

            Files.deleteIfExists(Paths.get(tempFilenames[i]));
            tempChannels.set(i, new RandomAccessFile(tempFilenames[i], "rw").getChannel());

            new CombSorter(sortBuffer, STRING_SIZE, partitionSizes[i], 2).sort();
            tempChannels.get(i).write(sortBuffer);

            currentPosition += partitionSizes[i];

        }

        final int TEMP_CAPACITY = 10 * STRING_SIZE;
        ArrayList<ByteBuffer> tempBuffers = new ArrayList<>(PARTITION_COUNT);
        for (int i = 0; i < PARTITION_COUNT; i++) {
            tempBuffers.add(null);
            tempBuffers.set(i, ByteBuffer.allocate(TEMP_CAPACITY));
            tempChannels.get(i).position(0);
            tempChannels.get(i).read(tempBuffers.get(i));
            tempBuffers.get(i).flip();
        }

        final int OUTBUF_CAPACITY = 10 * STRING_SIZE;
        Files.deleteIfExists(Paths.get(outputPath));
        outputChannel = new RandomAccessFile(outputPath, "rw").getChannel();
        ByteBuffer outputBuf = ByteBuffer.allocate(OUTBUF_CAPACITY);
        for ( ; tempBuffers.size() > 0; ) {
            int minInd = 0;
            for (int j = 1; j < tempBuffers.size(); j++) {
                //log.debug("Index compare minInd = " + minInd + "; j = " + j);
                if (0 < ByteBufferSorter
                        .compare(tempBuffers.get(minInd), tempBuffers.get(minInd).position() / STRING_SIZE,
                                tempBuffers.get(j), tempBuffers.get(j).position() / STRING_SIZE, STRING_SIZE)) {
                    minInd = j;
                }
            }
            ByteBuffer minBuf = tempBuffers.get(minInd);
            outputBuf.put(minBuf.array(), minBuf.position(), STRING_SIZE);
            minBuf.position(minBuf.position() + STRING_SIZE);

            if (Math.abs(outputBuf.position() - outputBuf.limit()) < STRING_SIZE) {
                outputBuf.flip();
                outputChannel.write(outputBuf);
                outputBuf.clear();
            }

            // Check that buffer is already read almost
            if (Math.abs(minBuf.position() - minBuf.limit()) < STRING_SIZE) {
                minBuf.clear();
                int count = tempChannels.get(minInd).read(minBuf);
                minBuf.flip();
                if (count <= 0) {
                    tempBuffers.remove(minInd);
                    tempChannels.get(minInd).close();
                    tempChannels.remove(minInd);
                }
            }
        }

        // TODO: write remainder data from outputBuf to outputChannel
        outputBuf.flip();
        outputChannel.write(outputBuf);
        outputChannel.close();


        //for (int i = 0; i < PARTITION_COUNT; i++)
            //Files.delete(Paths.get(tempFilenames[i]));

        System.out.println("Whole time = " + ChronoUnit.MILLIS.between(startTime, LocalTime.now()) + "ms.");
    }

}
