package mine.concurrent.lock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author HaoHao
 * Created on 2021/9/17.
 */
//@Slf4j(topic = "e")
public class MyLock {
    // 通过Unsafe类，调用 CAS方法，获取 status的偏移量
    private static Unsafe unsafe = null;
    // status 的偏移量  - 为什么是静态的
    private static long statusOffset;

    static {
        Field singleInstanceField = null;
        try {
            /**
             * class.getDeclaredField(String fieldName)  获取class中fieldName对应的字段
             * unsafe.objectFieldOffset(Field field)	 获取字段field在类中对应的地址偏移量
             */
            singleInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleInstanceField.setAccessible(true);
            unsafe = (Unsafe) singleInstanceField.get(null);
            statusOffset = unsafe.objectFieldOffset(MyLock.class.getDeclaredField("status"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 当前锁状态：0 - 无锁；1 - 有锁
    volatile int status = 0;
    // 持锁线程
    private transient Thread ownerThread = null;

    public boolean tryLock() {
        System.out.println(Thread.currentThread().getName() + " - tryLock");
        // 无锁就尝试加锁
        if (status == 0) {
            return lock();
        }
//        else { // 有锁就看持锁线程是不是自己，是则可以重入
//
//        }
        return false;
    }

    private boolean lock() {
        // 使用CAS尝试加锁
        if (compareAndSetState(0, 1)) {
            ownerThread = Thread.currentThread();
            System.out.println("加锁成功！当前线程为" + ownerThread.getName());
            return true;
        }
        return false;
    }

    public void unlock() {
        ownerThread = null;
        status = 0;
    }

    private boolean compareAndSetState(int expect, int update) {
        // See below for intrinsics setup to support this
        return unsafe.compareAndSwapInt(this, statusOffset, expect, update);
    }
}
