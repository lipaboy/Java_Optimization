package ru.nsu.fit.g14201.lipatkin.lab6;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;

public class Main {
    static MyLoader classMyLoader = new MyLoader();

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("You didn't enter the name of directory.");
                return ;
            }

            File directory = new File(args[0]);

            if (!directory.isDirectory()) {
                System.out.println("Directory with such name " + directory.getPath() + " doesn't exists.");
                return ;
            }

            String methodName = "getSecurityMessage";
            for (final File file : directory.listFiles()) {
                if (file.isFile() && file.getPath().endsWith(".class")) {
                    Class<?> c = classMyLoader.createClass(file);

                    Method[] methods = c.getMethods();
                    for (Method method : methods) {
                        if (method.getName().equals(methodName)
                                && method.getParameterTypes().length == 0) {
                            System.out.println(c.getName() + " "
                                    + method.invoke(c.newInstance(), new Object[0]));
                            break;
                        }
                    }
                }
            }
        } catch (IllegalAccessException
                | InvocationTargetException
                | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
