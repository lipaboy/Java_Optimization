package ru.nsu.fit.g14201.lipatkin.lab8;

import org.apache.log4j.Logger;
import ru.nsu.fit.g14201.lipatkin.lab7.TestGCPause;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by castiel on 13.12.2017.
 */
public class CombSorter extends MySorter {
    static private final Logger log = Logger.getLogger(CombSorter.class.getName());

    private final CyclicBarrier barrier;
    private final int threadCount;

    CombSorter(MyStarter starter, int threadCount) {
        super(starter);

        barrier = new CyclicBarrier(threadCount + 1);
        this.threadCount = threadCount;
    }

    @Override
    void sort() {
        LocalTime startTime = LocalTime.now();
        sortParallel();
        System.out.println("Expended time = " + ChronoUnit.MILLIS.between(startTime, LocalTime.now()) + "ms.");
    }

    private void sortParallel() {
        final float factorDecrease = 1.247f;
        float step = (MyStarter.TOTAL_STRINGS / factorDecrease);

        for (; ; step /= factorDecrease) {
            int stepRounded = Math.round(step);
            if (stepRounded >= threadCount && threadCount > 1) {
                for (int i = 0; i < threadCount; i++) {
                    int finalI = i;
                    new Thread(() -> sortByThread(finalI, stepRounded)).start();
                }
                log.debug("Before barrier");
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                log.debug("After barrier");
            }
            else {
                sortBySingle(stepRounded);
            }

            if (stepRounded <= 1)
                break;
        }
    }

    void sortByThread(int threadNum, int step) {
        // <threadCount, step>
        // TODO: you can optimize it (for cache locality property)
        log.debug(threadNum + " thread works, step = " + step);
        //for (int i = threadNum; i < step; i += threadCount) {
            int accumulator = threadNum;
            int lastJ = threadNum;
            for (int j = threadNum; j < MyStarter.TOTAL_STRINGS - step; ) {
                if (compare(j, j + step) > 0) {
                    swap(j, j + step);
                }
                accumulator += threadCount;
                if (accumulator >= step) {
                    j = lastJ + step;
                    lastJ += step;
                    accumulator = threadNum;
                }
                else
                    j += threadCount;
            }
        //}
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    void sortBySingle(int step) {
        if (step > 1) {
            for (int j = 0; j < MyStarter.TOTAL_STRINGS - step; j++) {
                if (compare(j, j + step) > 0) {
                    swap(j, j + step);
                }
            }
        }
        else {
            for (; ; ) {
                boolean swapped = false;
                for (int j = 0; j < MyStarter.TOTAL_STRINGS - 1; j++) {
                    if (compare(j, j + 1) > 0) {
                        swap(j, j + 1);
                        swapped = true;
                    }
                }
                if (!swapped)
                    break;
            }
        }
    }

}
