package mine.concurrent.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author HaoHao
 * Created on 2021/9/17.
 */
public class Locktest {

    static volatile int i = 1;

    public static void main(String[] args) {

        MyLock lock = new MyLock();

        Thread t1 = new Thread(() -> {
            while (true) {
                if (lock.tryLock()) {
                    System.out.println(Thread.currentThread().getName() + " : " + i);
                    i++;
                    lock.unlock();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                if (lock.tryLock()) {
                    System.out.println(Thread.currentThread().getName() + " : " + i);
                    i++;
                    lock.unlock();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
    }
}
