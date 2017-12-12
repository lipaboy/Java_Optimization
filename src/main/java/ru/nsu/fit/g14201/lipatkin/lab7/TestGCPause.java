package ru.nsu.fit.g14201.lipatkin.lab7;

/**
 * Created by castiel on 11.12.2017.
 */

        import java.util.ArrayList;
        import java.util.LinkedList;
        import java.util.List;
        import org.apache.log4j.Logger;
/**
 * Showcase from
 * https://blogs.oracle.com/vmrobot/entry/%D1%81%D0%B1%D0%BE%D1%80%D1%89%D0%B8%D0%BA_%D0%BC%D1%83%D1%81%D0%BE%D1%80%D0%B0_concurrent_mark_sweep
 */

public class TestGCPause {
    static private final Logger log = Logger.getLogger(TestGCPause.class.getName());
    /*
     * Класс для организации обмена сообщениями
     */
    static class Message {

        private long sendTime;

        private long maxPause;

        private boolean messageSent;

        private boolean messageReceived;

        public long getMaxPause() {
            return maxPause;
        }

        public synchronized void sendMessage() throws InterruptedException {
            // отправить сообщение и сохранить время отправки
            messageSent = true;
            sendTime = System.currentTimeMillis();
            notify();

            // ждать пока сообщение не будет получено
            while (!messageReceived)
                wait();

            messageReceived = false;
        }

        public synchronized void waitMessage() throws InterruptedException {
            // ждать сообщение
            while (!messageSent)
                wait();

            // определить время между отправкой и получением сообщения
            long receiveTime = System.currentTimeMillis();
            messageSent = false;

            // сохранить максимальное значение pause
            long pause = receiveTime - sendTime;
            if (pause > maxPause) {
                maxPause = pause;
            }

            // сообщить что сообщение было получено
            messageReceived = true;
            notify();
        }
    }

    // количество сообщений передаваемое во время тестирования
    static final int MESSAGE_NUMBER = 2000000;

    /*
     * Поток отправляющий сообщения
     */
    static class MessageSender extends Thread {

        private Message message;

        public MessageSender(Message message) {
            this.message = message;
        }

        public void run() {
            try {
                for (int i = 0; i < MESSAGE_NUMBER; i++) {
                    message.sendMessage();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Поток получающий сообщения
     */
    static class MessageReceiver extends Thread {

        private Message message;

        public MessageReceiver(Message message) {
            this.message = message;
        }

        public void run() {
            try {
                for (int i = 0; i < MESSAGE_NUMBER; i++) {
                    message.waitMessage();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Класс 'мусор', создающий в конструкторе несколько объектов
    static class Garbage {

        private List list = new LinkedList();

        Garbage() {
            for (int i = 0; i < 5; i++) {
                list.add(new byte[1]);
            }
        }
    }

    static List garbageStorage = new ArrayList();

    /*
     * Поток провоцирующий сборку мусора
     */
    static class GarbageProducer extends Thread {

        private volatile boolean stopped;

        public void run() {
            try {
                // период удаления созданного мусора
                long garbageRemovePeriod = 150;
                long lastGarbageRemoveTime = System.currentTimeMillis();

                // количество мусора, создаваемого за одну итерацию
                int garbageAmount = 50000;

                while (!stopped) {
                    for (int i = 0; i < garbageAmount; i++)
                        garbageStorage.add(new Garbage());

                    // периодически удаляем созданный мусор и даём возможность сборщику мусора очистить память
                    if (System.currentTimeMillis() > (lastGarbageRemoveTime + garbageRemovePeriod)) {
                        garbageStorage.clear();
                        Thread.sleep(100);
                        lastGarbageRemoveTime = System.currentTimeMillis();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void stopGarbageProducer() {
            stopped = true;
        }
    }

    static List oldGenGarbageStorage = new ArrayList();

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        /*
         * Заполняем память объектами, которые не могут быть удалены во время
         * работы приложения
         */
        long maxMemory = Runtime.getRuntime().maxMemory();
        System.out.println("JVM Maxmem: " + (Runtime.getRuntime().maxMemory() >> 20) + " Mb");
        log.info("JVM Maxmem: " + (Runtime.getRuntime().maxMemory() >> 20) + " Mb");
        long targetFreeMem = maxMemory / 2;

        while (Runtime.getRuntime().freeMemory() > targetFreeMem) {
            oldGenGarbageStorage.add(new Garbage());
        }

        Message message = new Message();

        GarbageProducer garbageProducer = new GarbageProducer();
        MessageSender messageSender = new MessageSender(message);
        MessageReceiver messageReceiver = new MessageReceiver(message);

        garbageProducer.start();
        messageReceiver.start();
        messageSender.start();

        messageReceiver.join();
        messageSender.join();

        garbageProducer.stopGarbageProducer();
        garbageProducer.join();

        System.out.println("\tTotal run time: \u001B[31;1m" + (System.currentTimeMillis() - start) + " ms\u001B[0m");
        System.out.println("\tMax GC pause: \u001B[31;1m" + message.getMaxPause() + " ms\u001B[0m");
        log.info("\tTotal run time: " + (System.currentTimeMillis() - start) + " ms");
        log.info("\tMax GC pause: " + message.getMaxPause() + " ms");
    }
}