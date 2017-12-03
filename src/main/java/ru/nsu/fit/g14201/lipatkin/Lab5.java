package ru.nsu.fit.g14201.lipatkin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by SPN on 03.12.2017.
 */
public class Lab5 {
    static <T> T setLogging(final T obj) {
        //todo get all interfaces: obj.getClass().getInterfaces()
        //todo Intercept with java.lang.reflect.Proxy
        //todo Log each interface call into System.out
        //todo OUTPUT EXAMPLE:
        //todo      CALLING:  java.lang.Runnable#run()
        //todo      RETURN VAL: null
        Class<?>[] interfaces = obj.getClass().getInterfaces();

        Object newObj = Proxy.newProxyInstance
                (obj.getClass().getClassLoader(),
                        interfaces,
                        new InvocationHandler() {
                            public Object invoke(Object proxy, Method method,
                                                 Object[] args) throws Throwable {
                                System.out.println("CALLING: " + method.getName());
                                Object retval = method.invoke(obj, args);
                                System.out.println("RETURN VAL: " + retval);
                                return retval;
                            }
                        });
        return (T) newObj;
    }

    public static void main( String[] args )
    {
        HashSet<Integer> plenty = new HashSet<Integer>();
        Set newPlenty = setLogging(plenty);
        newPlenty.add(5);
        newPlenty.contains(5);
    }
}
