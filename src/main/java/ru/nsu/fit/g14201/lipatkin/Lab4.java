package ru.nsu.fit.g14201.lipatkin;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Loader;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Method;


/**
 * Hello world!
 *
 */
public class Lab4
{
    public static class Calculator {

        public int sum(int x, int y) {
            return x + y;
        }
    }

    static Calculator createCalculator() throws Exception {
        //todo use JavaAssist to inherit Calculator class,
        //overload sum() method
        //and add 1 to original return value, so 2 + 2 will be 5

        ProxyFactory f = new ProxyFactory();
        f.setSuperclass(Calculator.class);
        f.setFilter(new MethodFilter() {
            public boolean isHandled(Method m) {
                return m.getName().equals("sum");
            }
        });
        Class c = f.createClass();
        MethodHandler mi = new MethodHandler() {
            public Object invoke(Object self, Method m, Method proceed,
                                 Object[] args) throws Throwable {
                return (int)proceed.invoke(self, args) + 1;  // execute the original method.
            }
        };
        Calculator cal = (Calculator)c.newInstance();
        ((Proxy)cal).setHandler(mi);

        return cal;
    }

//    public static class WrongCalculator extends Calculator {}

    public static void main( String[] args )
    {
        try {

            Calculator cal = createCalculator();
            System.out.println("2 + 2 = " + cal.sum(2, 2));
        } catch (Exception exp) {
            System.out.println("Exception_228: " + exp.getMessage());
        }
    }
}
