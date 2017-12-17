package ru.nsu.fit.g14201.lipatkin.lab8;

/**
 * Created by castiel on 12.12.2017.
 */

public class Main {

    /**
     * @param args      { inputPath, outputPath }
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            error();
            return;
        }

        try {
            new MyStarter().start(args[0], args[1], 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void error() {
        System.err.println("java -Xms1G -Xmx1G -jar filesorter.jar inputPath outputPath");
    }
}