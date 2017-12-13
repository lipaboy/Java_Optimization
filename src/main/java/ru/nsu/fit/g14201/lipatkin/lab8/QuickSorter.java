package ru.nsu.fit.g14201.lipatkin.lab8;

/**
 * Created by castiel on 12.12.2017.
 */
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class QuickSorter extends Sorter {

    private int maxLevel;

    private final CyclicBarrier barrier;
    private final byte[][] arrays;

    QuickSorter(Starter starter, int threadCount) {
        super(starter);

        maxLevel = 0;
        while ((threadCount /= 2) > 0)
            ++maxLevel;
        threadCount = 1 << maxLevel;

        barrier = new CyclicBarrier(threadCount);
        arrays = new byte[threadCount][Starter.STRING_SIZE];
    }

    @Override
    void sort() {
        sortParallel(0, 0, 0, Starter.TOTAL_STRINGS - 1);
    }

    private void sortParallel(int level, int threadNum, int from, int to) {
        if (level < maxLevel) {
            int center = partition(threadNum, from, to);
            new Thread(() -> sortParallel(level + 1, threadNum + (1 << level),
                    center + 1, to)).start();
            sortParallel(level + 1, threadNum, from, center - 1);

            return;
        }

        sort(threadNum, from, to);

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    private void sort(int threadNum, int from, int to) {
        if (from < to) {
            int center = partition(threadNum, from, to);
            sort(threadNum, from, center - 1);
            sort(threadNum, center + 1, to);
        }
    }

    private int partition(int threadNum, int from, int to) {
        int cmpPos = to * Starter.STRING_SIZE;
        byte[] cmpArray = arrays[threadNum];
        for (int i = 0; i < Starter.STRING_SIZE; i++) {
            cmpArray[i] = starter.sortBuffer.get(cmpPos + i);
        }

        int lo = from - 1;
        for (int hi = from; hi < to; hi++) {
            if (compare(hi, cmpArray) < 0) {
                ++lo;
                swap(lo, hi);
            }
        }

        if (compare(lo + 1, cmpArray) > 0)
            swap(lo + 1, to);

        return lo + 1;
    }
}