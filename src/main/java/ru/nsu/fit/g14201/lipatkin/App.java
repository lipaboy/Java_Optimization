package ru.nsu.fit.g14201.lipatkin;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Loader;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Method;


/**
 * Hello world!
 *
 */
public class App 
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
        return new Calculator();
    }

    public static class WrongCalculator extends Calculator {}

    public static void main( String[] args )
    {
        try {
            ClassPool pool = ClassPool.getDefault();
            Loader loader = new Loader(pool);
            System.out.println(pool.insertClassPath("./target/classes"));

            CtClass wrongCalcCt = pool.get(WrongCalculator.class.getName());
            //wrongCalcCt.setSuperclass(pool.get(Calculator.class.getName()));
            //CtMethod m = wrongCalcCt.getMethod("sum", "int, int");
            //m.insertBefore("{ System.out.println($1); System.out.println($2); }");
            //m.insertAfter("$_ += 1;");
            wrongCalcCt.writeFile();

//            Class wrongCalcClass = loader.loadClass(WrongCalculator.class.getName());
//            Object wrongCalc = wrongCalcClass.newInstance();

            Method toString = Class.forName(Calculator.class.getName()).getDeclaredMethod("sum");
            System.out.println(toString);

            //WrongCalculator wrongCalc = new WrongCalculator();
            Calculator cal = createCalculator();
            System.out.println("2 + 2 = " + cal.sum(2, 2));
        } catch (Exception exp) {
            System.out.println("Exception: " + exp.getMessage());
        }
    }
}
