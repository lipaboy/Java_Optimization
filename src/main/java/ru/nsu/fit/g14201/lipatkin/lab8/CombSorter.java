package ru.nsu.fit.g14201.lipatkin.lab8;

/**
 * Created by castiel on 13.12.2017.
 */
public class CombSorter extends MySorter {

    CombSorter(MyStarter starter) {
        super(starter);
    }

    @Override
    void sort() {
        final float factorDecrease = 1.247f;
        float step = (MyStarter.TOTAL_STRINGS / factorDecrease);

        int i;
        for (i = 0; ; step /= factorDecrease, i++) {
            int stepRounded = Math.round(step);

            if (stepRounded <= 1)
                break;
            for (int j = 0; j < MyStarter.TOTAL_STRINGS - stepRounded; j++) {
                if (compare(j, j + stepRounded) > 0) {
                    swap(j, j + stepRounded);
                }
            }

            System.out.println(stepRounded);
        }
        System.out.println("count iters = " + i);
        System.out.println("Log(n) = " + Math.log(MyStarter.TOTAL_STRINGS) / Math.log(factorDecrease));

        for ( ; ; ) {
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

        // Bubble sorter
//        for (int i = 0; i < MyStarter.TOTAL_STRINGS - 1; i++) {
//            for (int j = 0; j < MyStarter.TOTAL_STRINGS - i - 1; j++) {
//                if (compare(j, j + 1) > 0) {
//                    swap(j, j + 1);
//                }
//            }
//        }
    }
}
