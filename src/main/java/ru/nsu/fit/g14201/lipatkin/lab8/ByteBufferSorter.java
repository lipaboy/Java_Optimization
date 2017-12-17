package ru.nsu.fit.g14201.lipatkin.lab8;

import java.nio.ByteBuffer;

/**
 * Created by castiel on 13.12.2017.
 */
public abstract class ByteBufferSorter {
    ByteBuffer buffer;
    final int STRING_SIZE;
    final int TOTAL_SIZE;

    ByteBufferSorter(ByteBuffer sortBuffer, int stringSize, int totalSize) {
        this.buffer = sortBuffer;
        this.STRING_SIZE = stringSize;
        this.TOTAL_SIZE = totalSize;
    }

    public static int compare(ByteBuffer buffer1, int pos1, ByteBuffer buffer2, int pos2, int stringSize) {
        pos1 *= stringSize;
        pos2 *= stringSize;

        int res = 0;

        for (int i = 0; i < stringSize; i++) {
            res = buffer1.get(pos1 + i) - buffer2.get(pos2 + i);
            if (res != 0)
                break;
        }

        return res;
    }

    int compare(int pos1, int pos2) {
        return compare(buffer, pos1, buffer, pos2, STRING_SIZE);
    }

//    int compare(int pos, byte[] array) {
//        pos *= STRING_SIZE;
//
//        int res = 0;
//
//        for (int i = 0; i < STRING_SIZE; i++) {
//            res = buffer.get(pos + i) - array[i];
//            if (res != 0)
//                break;
//        }
//
//        return res;
//    }

    void swap(int pos1, int pos2) {
        pos1 *= STRING_SIZE;
        pos2 *= STRING_SIZE;

        byte tmp;

        for (int i = 0; i < STRING_SIZE; i++) {
            tmp = buffer.get(pos1 + i);
            buffer.put(pos1 + i, buffer.get(pos2 + i));
            buffer.put(pos2 + i, tmp);
        }
    }

    abstract void sort();
}
