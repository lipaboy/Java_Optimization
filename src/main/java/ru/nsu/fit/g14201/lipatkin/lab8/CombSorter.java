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
        for (int i = 0; i < MyStarter.TOTAL_STRINGS - 1; i++) {
            for (int j = 0; j < MyStarter.TOTAL_STRINGS - i - 1; j++) {
                if (compare(j, j + 1) > 0) {
                    swap(j, j + 1);
                }
            }
        }
    }
}
