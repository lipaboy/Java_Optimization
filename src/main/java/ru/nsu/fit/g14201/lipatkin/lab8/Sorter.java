package ru.nsu.fit.g14201.lipatkin.lab8;

/**
 * Created by castiel on 12.12.2017.
 */
import java.nio.ByteBuffer;

abstract class Sorter {

    Starter starter;

    Sorter(Starter starter) {
        this.starter = starter;
    }

    int compare(int pos1, int pos2) {
        pos1 *= Starter.STRING_SIZE;
        pos2 *= Starter.STRING_SIZE;

        final ByteBuffer buffer = starter.sortBuffer;
        int res = 0;

        for (int i = 0; i < Starter.STRING_SIZE; i++) {
            res = buffer.get(pos1 + i) - buffer.get(pos2 + i);
            if (res != 0)
                break;
        }

        return res;
    }

    int compare(int pos, byte[] array) {
        pos *= Starter.STRING_SIZE;

        final ByteBuffer buffer = starter.sortBuffer;
        int res = 0;

        for (int i = 0; i < Starter.STRING_SIZE; i++) {
            res = buffer.get(pos + i) - array[i];
            if (res != 0)
                break;
        }

        return res;
    }

    void swap(int pos1, int pos2) {
        pos1 *= Starter.STRING_SIZE;
        pos2 *= Starter.STRING_SIZE;

        final ByteBuffer buffer = starter.sortBuffer;
        byte tmp;

        for (int i = 0; i < Starter.STRING_SIZE; i++) {
            tmp = buffer.get(pos1 + i);
            buffer.put(pos1 + i, buffer.get(pos2 + i));
            buffer.put(pos2 + i, tmp);
        }
    }

    abstract void sort();
}