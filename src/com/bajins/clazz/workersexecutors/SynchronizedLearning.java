package com.bajins.clazz.workersexecutors;


/**
 * Synchronized示例
 *
 * @see Monitor https://www.cnblogs.com/tomsheep/archive/2010/06/09/1754419.html
 * @see java.util.concurrent.locks 锁类包
 * @see Lock
 * @see Condition
 * @see LockSupport
 * @see ReadWriteLock
 * @see ReentrantLock
 * @see ReentrantReadWriteLock
 * @see StampedLock
 * @see java.util.concurrent.atomic 原子类包
 * @see AtomicBoolean
 * @see AtomicInteger
 * @see AtomicIntegerArray
 * @see AtomicIntegerFieldUpdater
 * @see AtomicLong
 * @see AtomicLongArray
 * @see AtomicLongFieldUpdater
 * @see AtomicMarkableReference
 * @see AtomicReference
 * @see AtomicReferenceArray
 * @see AtomicReferenceFieldUpdater
 * @see AtomicStampedReference
 * @see Striped64 以下JDK1.8新增的原子类的抽象类
 * @see DoubleAccumulator
 * @see DoubleAdder
 * @see LongAccumulator
 * @see LongAdder
 * @see AbstractQueuedSynchronizer
 */
public class SynchronizedLearning {

    // 同步实例方法，锁住的是当前实例对象，不同对象实例访问时不会阻塞
    public synchronized void method() {
    }

    // 同步静态方法，锁住的是当前类，不同对象实例访问时都会被阻塞
    public static synchronized void methods() {
    }

    public void test() {
        // 同步代码块，锁住的是当前实例对象，相对于同步实例方法粒度更小
        synchronized (this) {
        }
        // 同步代码块，锁住的是类，可以是当前类或任意类
        synchronized (SynchronizedLearning.class) {
        }
        // 同步代码块，任意实例对象作为锁，锁住的是指定的实例对象，
        // 注意Integer、Character、Short、Long等有缓存，
        // 如果超出范围锁住的是新对象
        String lock = "";
        // String.intern()是一个Native方法,当调用 intern 方法时，
        // 如果常量池中已经该字符串，则返回池中的字符串；
        // 否则将此字符串添加到常量池中，并返回字符串的引用。
        synchronized (lock.intern()) {
        }
    }

    // 同步静态方法，锁住的是类，可以是当前类或任意类，
    // 不同对象实例访问时都会被阻塞
    public static void testStatic() {
        synchronized (SynchronizedLearning.class) {
        }
    }
}
