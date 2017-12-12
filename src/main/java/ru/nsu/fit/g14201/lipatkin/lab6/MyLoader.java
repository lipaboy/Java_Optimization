package ru.nsu.fit.g14201.lipatkin.lab6;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MyLoader extends ClassLoader {

    public Class<?> createClass(File file) {
        byte[] data = null;
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defineClass(null, data, 0, data.length);
    }
}
